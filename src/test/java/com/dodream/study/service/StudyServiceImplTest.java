package com.dodream.study.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class StudyServiceImplTest {
    @Mock
    private StudyRepository studyRepository;

    @Mock
    private StudyMemberRepository studyMemberRepository;

    @InjectMocks
    private StudyServiceImpl studyService;

    private User testUser;
    private Study testStudy;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .id(1L)
            .username("testUser")
            .provider("provider1")
            .providerId("1")
            .build();

        testStudy = Study.builder()
            .id(1L)
            .title("Test Study")
            .user(testUser)
            .category(Category.CATEGORY_ETC)
            .description("Study Description")
            .build();
    }

    @DisplayName("스터디 추가")
    @Test
    public void testAddStudy() {
        // given (사전 준비)
        StudyRequest studyRequest = StudyRequest.builder()
            .title("새로운 스터디")
            .username("테스트유저")
            .category(Category.CATEGORY_ETC)
            .description("Study Description")
            .build();

        when(studyRepository.save(any(Study.class))).thenReturn(testStudy);

        // when (테스트 진행할 행위)
        StudyResponse response = studyService.addStudy(testUser, studyRequest);

        // then (행위에 대한 결과 검증)
        assertEquals(testStudy.getId(), response.getId());
        assertEquals(testStudy.getTitle(), response.getTitle());
        assertEquals(testStudy.getUser().getUsername(), response.getUsername());
        assertEquals(testStudy.getCategory(), response.getCategory());
        assertEquals(testStudy.getDescription(), response.getDescription());
        verify(studyRepository, times(1)).save(any(Study.class));
    }

    @DisplayName("스터디 삭제")
    @Test
    public void testDeleteStudy() {
        // given (사전 준비)
        when(studyRepository.findById(1L)).thenReturn(Optional.of(testStudy));

        // when (테스트 진행할 행위)
        studyService.deleteStudy(testUser, 1L);

        // then (행위에 대한 결과 검증)
        verify(studyRepository, times(1)).delete(testStudy);
    }

    @DisplayName("유효하지 않은 사용자가 스터디 삭제")
    @Test
    public void testDeleteStudyAccessDenied() {
        // given (사전 준비)
        User invalidUser = User.builder()
            .id(2L)
            .build();

        when(studyRepository.findById(anyLong())).thenReturn(Optional.of(testStudy));

        // when (테스트 진행할 행위) + then (행위에 대한 결과 검증)
        BaseException exception = assertThrows(BaseException.class, () -> {
            studyService.deleteStudy(invalidUser, 1L);
        });

        assertEquals(ErrorCode.ACCESS_DENIED, exception.getErrorCode());
        verify(studyRepository, never()).delete(testStudy);
    }

    @DisplayName("나의 스터디 조회")
    @Test
    public void testGetMyStudy() {
        // given (사전 준비)
        PageRequest pageable = PageRequest.of(0, 12);

        Page<StudyResponse> page = new PageImpl<>(List.of(
            new StudyResponse(testStudy.getId(), testStudy.getTitle(), testUser.getUsername(),
                testUser.getProfileImage(), testStudy.getDescription(), testStudy.getCategory(), 5L,
                testStudy.getUpdatedAt(), testStudy.getCreatedAt())
        ));
        when(studyMemberRepository.findByUserAndRoleIn(pageable, testUser,
            List.of(RoleEnum.ROLE_MEMBER, RoleEnum.ROLE_LEADER)))
            .thenReturn(page);

        // when (테스트 진행할 행위)
        Page<StudyResponse> studyList = studyService.getMyStudyList(pageable, testUser);

        // then (행위에 대한 결과 검증)
        assertNotNull(studyList);
        assertEquals(1, studyList.getTotalElements());
        assertEquals(testStudy.getTitle(), studyList.getContent().get(0).getTitle());
        verify(studyMemberRepository, times(1))
            .findByUserAndRoleIn(pageable, testUser, List.of(RoleEnum.ROLE_MEMBER, RoleEnum.ROLE_LEADER));
    }

    @DisplayName("스터디 전체 조회")
    @Test
    public void testGetStudyList() {
        // given (사전 준비)
        Pageable pageable = PageRequest.of(0, 12);
        Page<StudyResponse> studyPage = new PageImpl<>(List.of(new StudyResponse(testStudy)));

        when(studyRepository.findAllStudy(any(Pageable.class))).thenReturn(studyPage);

        // when (테스트 진행할 행위)
        Page<StudyResponse> result = studyService.getStudyList(testUser, pageable, null);

        // then (행위에 대한 결과 검증)
        assertEquals(1, result.getContent().size());
        verify(studyRepository).findAllStudy(any(Pageable.class));
    }

    @DisplayName("검색어 조회 - 제목 + 내용 or 작성자")
    @Test
    public void testSearchStudyKeyword() {
        // given (사전 준비)
        String keyword = "Test";
        Pageable pageable = PageRequest.of(0, 12);  // 첫 번째 페이지, 10개의 스터디를 조회
        List<StudyResponse> studyResponseList = List.of(new StudyResponse(testStudy));
        Page<StudyResponse> studyPage = new PageImpl<>(studyResponseList,
            pageable, studyResponseList.size());

        when(studyRepository.findStudiesByTitleDescriptionOrUsername(pageable, keyword))
            .thenReturn(studyPage);

        // when (테스트 진행할 행위)
        Page<StudyResponse> result = studyService.searchStudiesByKeyword(pageable, testUser, keyword);

        // then (행위에 대한 결과 검증)
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Study", result.getContent().get(0).getTitle());
        verify(studyRepository).findStudiesByTitleDescriptionOrUsername(pageable, keyword);
    }

    @DisplayName("매칭되는 검색어가 없는 경우 (스터디가 없는 경우)")
    @Test
    public void testSearchStudyKeywordWhenNoMatches() {
        // given (사전 준비)
        String keyword = "Java";
        Pageable pageable = PageRequest.of(0, 12);
        Page<StudyResponse> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(studyRepository.findStudiesByTitleDescriptionOrUsername(pageable, keyword))
            .thenReturn(emptyPage);

        // when (테스트 진행할 행위)
        Page<StudyResponse> result = studyService.searchStudiesByKeyword(pageable, testUser, keyword);

        // then (행위에 대한 결과 검증)
        assertTrue(result.getContent().isEmpty());
        verify(studyRepository).findStudiesByTitleDescriptionOrUsername(pageable, keyword);
    }

    @DisplayName("스터디 검색어 찾는 과정에서 예외 발생")
    @Test
    public void testSearchStudyKeywordOccurException() {
        // given (사전 준비)
        String keyword = "유효하지 않은 키워드...";
        Pageable pageable = PageRequest.of(0, 12);

        when(studyRepository.findStudiesByTitleDescriptionOrUsername(pageable, keyword))
            .thenThrow(IllegalArgumentException.class);

        // when (테스트 진행할 행위) + then (행위에 대한 결과 검증)
        BaseException exception = assertThrows(BaseException.class, () ->
            studyService.searchStudiesByKeyword(pageable, testUser, keyword)
        );

        assertEquals(ErrorCode.STUDY_SEARCH_NOT_FOUND, exception.getErrorCode());
        verify(studyRepository).findStudiesByTitleDescriptionOrUsername(pageable, keyword);
    }

    @DisplayName("스터디 카테고리별 조회")
    @Test
    public void testGetCategoryStudyList() {
        // given (사전 준비)
        Pageable pageable = PageRequest.of(0, 12);
        String validCategory = "CATEGORY_CS"; // 유효한 카테고리
        Page<StudyResponse> studyPage = new PageImpl<>(List.of(new StudyResponse(testStudy)));

        when(studyRepository.findByStudyCategory(any(Pageable.class), eq(Category.CATEGORY_CS)))
            .thenReturn(studyPage);

        // when (테스트 진행할 행위)
        Page<StudyResponse> result =
            studyService.getStudyList(testUser, pageable, validCategory);

        // then (행위에 대한 결과 검증)
        assertEquals(1, result.getContent().size());
        assertEquals("Test Study", result.getContent().get(0).getTitle());
        verify(studyRepository).findByStudyCategory(any(Pageable.class), eq(Category.CATEGORY_CS));
    }

    @DisplayName("알 수 없는 스터디 카테고리 조회")
    @Test
    public void testGetInvalidCategoryStudyList() {
        // given (사전 준비)
        Pageable pageable = PageRequest.of(0, 12);

        // when (테스트 진행할 행위)
        BaseException exception = assertThrows(BaseException.class, () ->
            studyService.getStudyList(testUser, pageable, "INVALID_CATEGORY")
        );

        // then (행위에 대한 결과 검증)
        assertEquals(ErrorCode.STUDY_CATEGORY_ERROR, exception.getErrorCode());
    }

    @DisplayName("스터디 수정")
    @Test
    public void testUpdateStudy() {
        // given (사전 준비)
        StudyUpdateRequest updateRequest = new StudyUpdateRequest();
        updateRequest.setTitle("새로운 제목으로 수정");
        updateRequest.setDescription("새로운 설명으로 업데이트");

        when(studyRepository.findById(1L)).thenReturn(Optional.of(testStudy));

        // when (테스트 진행할 행위)
        StudyUpdateResponse response = studyService.updateStudy(testUser, 1L, updateRequest);

        // then (행위에 대한 결과 검증)
        assertThat(response.getTitle()).isEqualTo("새로운 제목으로 수정");
        verify(studyRepository).findById(1L);
    }

    @DisplayName("유효하지 않은 사용자가 스터디 수정")
    @Test
    public void testUpdateStudyAccessDenied() {
        // given (사전 준비)
        User otherUser = User.builder()
            .id(2L)
            .build();

        StudyUpdateRequest updateRequest = new StudyUpdateRequest();
        updateRequest.setTitle("새로운 제목");
        updateRequest.setDescription("새로운 설명");

        when(studyRepository.findById(1L)).thenReturn(Optional.of(testStudy));

        // when (테스트 진행할 행위) + then (행위에 대한 결과 검증)
        BaseException exception = assertThrows(BaseException.class, () ->
            studyService.updateStudy(otherUser, 1L, updateRequest)
        );

        assertEquals(ErrorCode.ACCESS_DENIED, exception.getErrorCode());
        verify(studyRepository).findById(1L);
        verify(studyRepository, never()).save(any(Study.class));
    }

    @DisplayName("수정하려는 스터디가 없는 경우")
    @Test
    public void testUpdateStudyNotFound() {
        // given (사전 준비)
        StudyUpdateRequest updateRequest = new StudyUpdateRequest();
        updateRequest.setTitle("새로운 제목");
        updateRequest.setDescription("새로운 설명");

        when(studyRepository.findById(1L)).thenReturn(Optional.empty());

        // when (테스트 진행할 행위) + then (행위에 대한 결과 검증)
        BaseException exception = assertThrows(BaseException.class, () ->
            studyService.updateStudy(testUser, 1L, updateRequest)
        );

        assertEquals(ErrorCode.STUDY_NOT_FOUND, exception.getErrorCode());
        verify(studyRepository).findById(1L);
    }

}