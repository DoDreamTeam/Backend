package com.dodream.study.service;

import com.dodream.common.exception.BaseException;
import com.dodream.common.exception.ErrorCode;
import com.dodream.study.domain.StudyMemberRequest;
import com.dodream.study.domain.StudyMemberResponse;
import com.dodream.study.domain.StudyMemberUpdateRequest;
import com.dodream.study.domain.StudyMemberUpdateResponse;
import com.dodream.study.entity.Study;
import com.dodream.study.entity.StudyMember;
import com.dodream.study.enumtype.RoleEnum;
import com.dodream.study.repository.StudyMemberRepository;
import com.dodream.study.repository.StudyRepository;
import com.dodream.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudyMemberServiceImpl implements StudyMemberService {

    private final StudyMemberRepository studyMemberRepository;
    private final StudyRepository studyRepository;

    // 스터디 회원 조회 (ROLE_LEADER 만 가능 - ROLE_MEMBER 만 조회되어야 함)
    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "getStudyMembers")
    public Page<StudyMemberResponse> getStudyMembers(Long studyId, User user, Pageable pageable) {
        checkUserRole(studyId, user);
        Page<StudyMember> studyMembers =
            studyMemberRepository.findByStudyMemberId(studyId, RoleEnum.ROLE_MEMBER, pageable);

        List<StudyMemberResponse> memberResponses = getStudyMemberResponses(studyMembers);

        return new PageImpl<>(memberResponses, pageable, studyMembers.getTotalElements());
    }

    private static List<StudyMemberResponse> getStudyMemberResponses(
        Page<StudyMember> studyMembers) {
        return studyMembers.stream()
            .map(studyMember -> StudyMemberResponse.builder()
                .username(studyMember.getUser().getUsername())
                .joinDate(studyMember.getJoinDate())
                .build())
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudyMemberResponse> getStudyApplyMembers(Long studyId, User user,
        Pageable pageable) {
        checkUserRole(studyId, user);
        Page<StudyMember> studyMembers =
            studyMemberRepository.findByStudyMemberId(studyId, RoleEnum.ROLE_WAITING, pageable);

        List<StudyMemberResponse> memberResponses = getStudyMemberResponses(studyMembers);

        return new PageImpl<>(memberResponses, pageable, studyMembers.getTotalElements());
    }

    @Override
    @Transactional
    public StudyMemberResponse addStudyMember(User user, StudyMemberRequest studyMemberRequest) {
        Optional<Study> studyOptional = studyRepository.findById(studyMemberRequest.getStudyId());
        if (studyOptional.isEmpty()) {
            throw new BaseException(ErrorCode.STUDY_NOT_FOUND);
        }

        Study study = studyOptional.get();

        Optional<StudyMember> existingMemberOptional = studyMemberRepository.findByUserIdAndStudyId(user.getId(), study.getId());
        if (existingMemberOptional.isPresent()) {   // 이미 신청한 경우와 스터디 멤버인 경우 구분
            RoleEnum role = existingMemberOptional.get().getRole();
            if (role == RoleEnum.ROLE_WAITING) {
                throw new BaseException(ErrorCode.ALREADY_APPLIED_TO_STUDY);
            } else if (role == RoleEnum.ROLE_MEMBER || role == RoleEnum.ROLE_LEADER) {
                throw new BaseException(ErrorCode.ALREADY_STUDY_MEMBER);
            }
        }

        StudyMember studyMember = studyMemberRequest.toEntity(user, study);
        StudyMember savedStudyMember = studyMemberRepository.save(studyMember);

        return StudyMemberResponse.builder()
            .id(savedStudyMember.getId())
            .username(user.getUsername())
            .joinDate(savedStudyMember.getJoinDate())
            .build();
    }

    @Override
    @Transactional
    public StudyMemberUpdateResponse updateStudyMember(User user, Long memberId,
        StudyMemberUpdateRequest studyMemberUpdateRequest) {
        StudyMember studyMember = getStudyMember(memberId);
        checkUserRole(studyMember.getStudy().getId(), user);

        studyMember.updateStudyMember(studyMemberUpdateRequest.getRole());

        return StudyMemberUpdateResponse.builder()
            .id(studyMember.getId())
            .roleEnum(studyMember.getRole())
            .joinDate(LocalDateTime.now())
            .build();
    }

    // ROLE이 ROLE_WATING 또는 ROLE_MEMBER인 경우만 삭제 (ROLE_LEADER 삭제 X)
    @Override
    public void deleteStudyMember(User user, Long memberId) {
        StudyMember studyMember = getStudyMember(memberId);
        checkUserRole(studyMember.getStudy().getId(), user);

        studyMemberRepository.delete(studyMember);
    }

    @Override
    @Transactional
    public StudyMemberUpdateResponse transferLeader(User user, Long currentLeaderId,
        Long newLeaderId) {
        StudyMember currentLeader = getStudyMember(currentLeaderId);
        if (!currentLeader.getRole().equals(RoleEnum.ROLE_LEADER)) {
            throw new BaseException(ErrorCode.INVALID_CURRENT_LEADER);
        }

        // 리더 권한 여부 확인
        checkUserRole(currentLeader.getStudy().getId(), user);
        StudyMember newLeader = getStudyMember(newLeaderId);
        if (!newLeader.getRole().equals(RoleEnum.ROLE_MEMBER)) {
            throw new BaseException(ErrorCode.INVALID_NEW_LEADER);
        }

        // 기존 리더는 멤버로, 특정 멤버는 리더로 권한 변경
        currentLeader.updateStudyMember(RoleEnum.ROLE_MEMBER);
        newLeader.updateStudyMember(RoleEnum.ROLE_LEADER);

        Study study = currentLeader.getStudy();
        study.setUser(newLeader.getUser());

        // study의 user_id를 newLeaderId로 변경
        studyRepository.save(study);

        return StudyMemberUpdateResponse.builder()
            .id(newLeader.getId())
            .roleEnum(newLeader.getRole())
            .joinDate(LocalDateTime.now())
            .build();
    }

    private void checkUserRole(Long studyId, User user) {
        Optional<RoleEnum> role =
            studyMemberRepository.findRoleByStudyIdAndUserId(studyId, user.getId());

        // 권한이 ROLE_LEADER 일 때 수정, 삭제 가능!
        if (role.isEmpty() || role.get() != RoleEnum.ROLE_LEADER) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }
    }

    private StudyMember getStudyMember(Long id) {
        return studyMemberRepository.findById(id)
            .orElseThrow(() -> new BaseException(ErrorCode.STUDY_MEMBER_NOT_FOUND));
    }

}
