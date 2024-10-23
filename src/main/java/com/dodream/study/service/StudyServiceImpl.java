package com.dodream.study.service;

import com.dodream.common.enumtype.Category;
import com.dodream.common.exception.BaseException;
import com.dodream.common.exception.ErrorCode;
import com.dodream.study.domain.StudyRequest;
import com.dodream.study.domain.StudyResponse;
import com.dodream.study.domain.StudyUpdateRequest;
import com.dodream.study.domain.StudyUpdateResponse;
import com.dodream.study.entity.Study;
import com.dodream.study.enumtype.RoleEnum;
import com.dodream.study.repository.StudyMemberRepository;
import com.dodream.study.repository.StudyRepository;
import com.dodream.user.entity.User;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudyServiceImpl implements StudyService {

    private final StudyRepository studyRepository;
    private final StudyMemberRepository studyMemberRepository;

    // 스터디 멤버 상태 가져오기
    private String getStatusStudyMember(Long studyId, Long userId) {
        Optional<RoleEnum> role = studyMemberRepository.findRoleByStudyIdAndUserId(studyId, userId);
        return role.map(RoleEnum::getRole).orElse(null);
    }

    // 메인 페이지 스터디 조회 (12개씩) - 비회원 + 회원 포함
    // 메인 페이지 인기 스터디 조회 (인원수별 조회)
    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "getStudy")
    public Page<StudyResponse> getStudyList(User loginedUser, Pageable pageable, String category) {
        Sort sort = pageable.getSort();

        // StudyMember 수로 정렬하는지 확인
        boolean sortByMemberCount = sort.stream()
            .anyMatch(order -> order.getProperty().equals("count"));

        Page<StudyResponse> studyList;
        if (sortByMemberCount) {
            studyList = studyRepository.findAllStudyWithMemberCount(pageable);
        } else {
            if (category != null) {
                try {
                    studyList = studyRepository.findByStudyCategory(pageable,
                        Category.valueOf(category));
                } catch (IllegalArgumentException e) {
                    throw new BaseException(ErrorCode.STUDY_CATEGORY_ERROR);
                }
            } else {
                studyList = studyRepository.findAllStudy(pageable);

                if (loginedUser != null) {
                    studyList.getContent().forEach(s ->
                        s.setStatus(getStatusStudyMember(s.getId(), loginedUser.getId()))
                    );
                }
            }
        }

        return studyList;
    }

    // 검색어 (제목 + 내용 or 작성자) 조회
    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "searchStudy")
    public Page<StudyResponse> searchStudiesByKeyword(Pageable pageable, String keyword) {
        try {
            return studyRepository.findStudiesByTitleDescriptionOrUsername(pageable, keyword);
        } catch (IllegalArgumentException e) {
            throw new BaseException(ErrorCode.STUDY_SEARCH_NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public StudyResponse addStudy(User user, StudyRequest studyRequest) {
        Study study = studyRequest.toEntity(user);
        Study savedStudy = studyRepository.save(study);

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
        Study study = studyRepository.findById(id)
            .orElseThrow(() -> new BaseException(ErrorCode.STUDY_NOT_FOUND));

        // 스터디 소유자 확인
        if (!study.getUser().getId().equals(user.getId())) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }

        studyRepository.delete(study);
    }

    @Override
    @Transactional
    public StudyUpdateResponse updateStudy(User user, Long id,
        StudyUpdateRequest studyUpdateRequest) {
        Study study = studyRepository.findById(id)
            .orElseThrow(() -> new BaseException(ErrorCode.STUDY_NOT_FOUND));

        // 스터디 소유자 확인
        if (!study.getUser().getId().equals(user.getId())) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }

        study.updateStudy(studyUpdateRequest.getTitle(), studyUpdateRequest.getDescription());

        return StudyUpdateResponse.builder()
            .id(study.getId())
            .title(study.getTitle())
            .description(study.getDescription())
            .build();
    }

    // 내가 참여중인 스터디 조회
    // StudyMemberRepository를 통해 ROLE_MEMBER 또는 ROLE_LEADER에 해당하는 스터디 조회
    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "myStudy")
    public Page<StudyResponse> getMyStudyList(Pageable pageable, User user) {
        return studyMemberRepository.findByUserAndRoleIn(pageable, user,
                List.of(RoleEnum.ROLE_MEMBER, RoleEnum.ROLE_LEADER))
            .map(studyMember -> new StudyResponse(studyMember.getStudy()));
    }

}
