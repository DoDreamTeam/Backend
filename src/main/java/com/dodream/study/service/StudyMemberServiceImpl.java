package com.dodream.study.service;

import com.dodream.common.exception.BaseException;
import com.dodream.common.exception.ErrorCode;
import com.dodream.study.domain.StudyMemberResponse;
import com.dodream.study.entity.StudyMember;
import com.dodream.study.enumtype.RoleEnum;
import com.dodream.study.repository.StudyMemberRepository;
import com.dodream.user.entity.User;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudyMemberServiceImpl implements StudyMemberService {

    private final StudyMemberRepository studyMemberRepository;

    // 스터디 회원 조회 (ROLE_LEADER 만 가능 - ROLE_MEMBER 만 조회되어야 함)
    @Override
    @Transactional(readOnly = true)
    public Page<StudyMemberResponse> getStudyMembers(Long studyId, User user, Pageable pageable) {
        checkUserRole(studyId, user);
        Page<StudyMember> studyMembers =
            studyMemberRepository.findByStudyMemberId(studyId, RoleEnum.ROLE_MEMBER, pageable);

        List<StudyMemberResponse> memberResponses = studyMembers.stream()
            .map(studyMember -> StudyMemberResponse.builder()
                .username(studyMember.getUser().getUsername())
                .joinDate(studyMember.getJoinDate())
                .build())
            .toList();

        return new PageImpl<>(memberResponses, pageable, studyMembers.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudyMemberResponse> getStudyApplyMembers(Long studyId, User user,
        Pageable pageable) {
        checkUserRole(studyId, user);
        Page<StudyMember> studyMembers =
            studyMemberRepository.findByStudyMemberId(studyId, RoleEnum.ROLE_WAITING, pageable);

        List<StudyMemberResponse> memberResponses = studyMembers.stream()
            .map(studyMember -> StudyMemberResponse.builder()
                .username(studyMember.getUser().getUsername())
                .joinDate(studyMember.getJoinDate())
                .build())
            .toList();

        return new PageImpl<>(memberResponses, pageable, studyMembers.getTotalElements());
    }

    private void checkUserRole(Long studyId, User user) {
        Optional<RoleEnum> role =
            studyMemberRepository.findRoleByStudyIdAndUserId(studyId, user.getId());

        // 권한이 ROLE_LEADER 일 때 수정, 삭제 가능!
        if (role.isEmpty() || role.get() != RoleEnum.ROLE_LEADER) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }
    }

}
