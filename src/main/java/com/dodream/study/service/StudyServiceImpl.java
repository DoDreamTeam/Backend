package com.dodream.study.service;

import com.dodream.common.enumtype.Category;
import com.dodream.common.exception.BaseException;
import com.dodream.common.exception.ErrorCode;
import com.dodream.study.domain.StudyRequest;
import com.dodream.study.domain.StudyResponse;
import com.dodream.study.domain.StudyUpdateRequest;
import com.dodream.study.domain.StudyUpdateResponse;
import com.dodream.study.entity.Study;
import com.dodream.study.entity.StudyMember;
import com.dodream.study.enumtype.RoleEnum;
import com.dodream.study.repository.StudyMemberRepository;
import com.dodream.study.repository.StudyRepository;
import com.dodream.user.entity.User;
import com.dodream.util.CustomPageImpl;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudyServiceImpl implements StudyService {

    private final StudyRepository studyRepository;
    private final StudyMemberRepository studyMemberRepository;

    private String getStatusStudyMember(Long studyId, Long userId) {
        Optional<RoleEnum> role = studyMemberRepository.findRoleByStudyIdAndUserId(studyId, userId);
        return role.map(RoleEnum::getRole).orElse(null);
    }

    // 메인 페이지 스터디 조회 (12개씩) - 비회원 + 회원 포함
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "studyList", key = "#loginedUser != null ? #loginedUser.id + '_' + "
        + "#category : 'user_' + #category", unless = "#result.isEmpty()")
    public Page<StudyResponse> getStudyList(User loginedUser, Pageable pageable, String category) {
        List<String> roles = Arrays.asList(RoleEnum.ROLE_MEMBER.getRole(), RoleEnum.ROLE_LEADER.getRole());
        Page<StudyResponse> studyList;

        if (category != null) {
            // 카테고리별 조회
            try {
                studyList = studyRepository.findByStudyCategory(pageable, Category.valueOf(category), roles);
            } catch (IllegalArgumentException e) {
                throw new BaseException(ErrorCode.STUDY_CATEGORY_ERROR);
            }
        } else {
            studyList = studyRepository.findAllStudy(pageable);
        }

        if (loginedUser != null) {
            studyList.getContent().forEach(study ->
                study.setStatus(getStatusStudyMember(study.getId(), loginedUser.getId()))
            );
        }

        return new CustomPageImpl<>(studyList.getContent(), pageable, studyList.getTotalElements());
    }

    // 검색어 (제목 + 내용 or 작성자) 조회
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "studySearch",
        key = "#keyword + '_' + (#loginedUser != null ? #loginedUser.id : 'guest')",
        unless = "#result.isEmpty()")
    public Page<StudyResponse> searchStudiesByKeyword(Pageable pageable, User loginedUser, String keyword) {
        try {
            List<String> roles = Arrays.asList(RoleEnum.ROLE_MEMBER.getRole(), RoleEnum.ROLE_LEADER.getRole());
            Page<StudyResponse> studyList =
                studyRepository.findStudiesByTitleDescriptionOrUsername(pageable, keyword, roles);

            // 로그인한 사용자가 있을 경우 role 조회 및 설정
            if (loginedUser != null) {
                studyList.getContent().forEach(study ->
                    study.setStatus(getStatusStudyMember(study.getId(), loginedUser.getId()))
                );
            }

            return new CustomPageImpl<>(studyList.getContent(), pageable, studyList.getTotalElements());
        } catch (IllegalArgumentException e) {
            throw new BaseException(ErrorCode.STUDY_SEARCH_NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public StudyResponse addStudy(User user, StudyRequest studyRequest) {
        Study study = studyRequest.toEntity(user);
        Study savedStudy = studyRepository.save(study);

        StudyMember studyMember = StudyMember.builder()
            .user(user)
            .study(savedStudy)
            .role(RoleEnum.ROLE_LEADER)
            .joinDate(LocalDateTime.now())
            .build();

        studyMemberRepository.save(studyMember);

        return StudyResponse.builder()
            .id(savedStudy.getId())
            .title(savedStudy.getTitle())
            .username(user.getUsername())
            .description(savedStudy.getDescription())
            .userCount(0L)
            .createdAt(savedStudy.getCreatedAt())
            .updatedAt(savedStudy.getUpdatedAt())
            .category(Category.valueOf(savedStudy.getCategory().name()))
            .build();
    }

    @Override
    @Transactional
    public void deleteStudy(User user, Long id) {
        Study study = getStudy(id);
        checkUserRole(study.getId(), user);

        studyRepository.delete(study);
    }

    private Study getStudy(Long id) {
        return studyRepository.findById(id)
            .orElseThrow(() -> new BaseException(ErrorCode.STUDY_NOT_FOUND));
    }

    @Override
    @Transactional
    public StudyUpdateResponse updateStudy(User user, Long id,
        StudyUpdateRequest studyUpdateRequest) {
        Study study = getStudy(id);
        checkUserRole(study.getId(), user);
        study.updateStudy(studyUpdateRequest.getTitle(), studyUpdateRequest.getDescription());

        return StudyUpdateResponse.builder()
            .id(study.getId())
            .title(study.getTitle())
            .description(study.getDescription())
            .build();
    }

    // 내가 참여중인 스터디 조회 (스터디 제목, 유저명, 스터디 참가 인원수)
    // StudyMemberRepository를 통해 ROLE_MEMBER 또는 ROLE_LEADER에 해당하는 스터디 조회
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "myStudyList", key = "#user.id", unless = "#result.isEmpty()")
    public Page<StudyResponse> getMyStudyList(Pageable pageable, User user) {
        Page<StudyResponse> myStudyList = studyMemberRepository.findByUserAndRoleIn(pageable, user,
            List.of(RoleEnum.ROLE_MEMBER, RoleEnum.ROLE_LEADER));

        List<StudyResponse> studyResponse = myStudyList.stream()
            .map(study -> StudyResponse.builder()
                .id(study.getId())
                .userId(study.getUserId())
                .title(study.getTitle())
                .category(study.getCategory())
                .username(study.getUsername())
                .userCount(study.getUserCount())
                .profileImage(study.getProfileImage())
                .build())
            .toList();

        return new CustomPageImpl<>(studyResponse, pageable, myStudyList.getTotalElements());
    }

    // 인기 스터디 조회
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "popularStudyList")
    public Page<StudyResponse> getPopularStudyList(Pageable pageable, User loginedUser, Long userCount) {
        List<String> roles = Arrays.asList(RoleEnum.ROLE_MEMBER.getRole(), RoleEnum.ROLE_LEADER.getRole());
        Page<StudyResponse> studyList = studyRepository.findAllStudyWithMemberCount(pageable, roles);

        // 로그인한 사용자가 있을 경우 role 조회 및 설정
        if (loginedUser != null) {
            studyList.getContent().forEach(study ->
                study.setStatus(getStatusStudyMember(study.getId(), loginedUser.getId()))
            );
        }

        return new CustomPageImpl<>(studyList.getContent(), pageable, studyList.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public StudyResponse findStudy(Long id, User user) {
        Study study = studyRepository.findById(id)
            .orElseThrow(() -> new BaseException(ErrorCode.STUDY_NOT_FOUND));

        StudyMember studyMember = studyMemberRepository.findByStudyAndUser(study, user)
            .orElseThrow(() -> new BaseException(ErrorCode.STUDY_MEMBER_NOT_FOUND));

        if (studyMember.getRole() != RoleEnum.ROLE_MEMBER && studyMember.getRole() != RoleEnum.ROLE_LEADER) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }

        return StudyResponse.builder()
            .id(study.getId())
            .title(study.getTitle())
            .userId(study.getUser().getId())
            .username(study.getUser() != null ? study.getUser().getUsername() : null)
            .profileImage(study.getUser() != null ? study.getUser().getProfileImage() : null)
            .description((study.getDescription()))
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public String existStudy(Long id, User user) {
        return studyMemberRepository.findRoleByStudyIdAndUser(id, user).orElse(null);
    }

    private void checkUserRole(Long study, User user) {
        Optional<RoleEnum> role =
            studyMemberRepository.findRoleByStudyIdAndUserId(study, user.getId());

        // 권한이 ROLE_LEADER 일 때 수정, 삭제 가능!
        if (role.isEmpty() || role.get() != RoleEnum.ROLE_LEADER) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }
    }
}
