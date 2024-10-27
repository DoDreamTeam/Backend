package com.dodream.study.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dodream.common.enumtype.Category;
import com.dodream.common.exception.BaseException;
import com.dodream.common.exception.ErrorCode;
import com.dodream.study.domain.NoticeRequest;
import com.dodream.study.domain.NoticeResponse;
import com.dodream.study.domain.UpdateNoticeRequest;
import com.dodream.study.domain.UpdateNoticeResponse;
import com.dodream.study.entity.Notice;
import com.dodream.study.entity.Study;
import com.dodream.study.enumtype.RoleEnum;
import com.dodream.study.repository.NoticeRepository;
import com.dodream.study.repository.StudyMemberRepository;
import com.dodream.study.repository.StudyRepository;
import com.dodream.user.entity.User;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NoticeServiceTest {

    @InjectMocks
    private NoticeServiceImpl noticeService;

    @Mock
    private NoticeRepository noticeRepository;

    @Mock
    private StudyRepository studyRepository;

    @Mock
    private StudyMemberRepository studyMemberRepository;

    private User user;
    private Study study;
    private Notice notice;

    @BeforeEach
    void setUp() {
        user = User.builder()
            .id(1L)
            .username("testUser")
            .provider("provider1")
            .providerId("1")
            .build();

        study = Study.builder()
            .id(1L)
            .title("Test Study")
            .user(user)
            .category(Category.CATEGORY_ETC)
            .description("Study Description")
            .build();

        notice = Notice.builder()
            .id(1L)
            .study(study)
            .content("테스트 공지사항")
            .build();

    }

    @DisplayName("공지사항 추가")
    @Test
    public void testCreateNotice() {
        // given (사전 준비)
        NoticeRequest request = NoticeRequest.builder()
            .content("새로운 공지사항")
            .isDeleted(false)
            .build();

        // when (테스트 진행할 행위)
        when(studyRepository.findById(study.getId())).thenReturn(Optional.of(study));
        when(noticeRepository.findDeletedEmptyContentNoticeByStudyId(study.getId())).thenReturn(Optional.empty());
        when(studyMemberRepository.findRoleByStudyIdAndUserId(study.getId(), user.getId())).thenReturn(Optional.of(
            RoleEnum.ROLE_LEADER));
        when(noticeRepository.save(any(Notice.class))).thenReturn(notice);

        NoticeResponse response = noticeService.createNotice(study.getId(), request, user);

        // then (행위에 대한 결과 검증)
        assertThat(response.getContent()).isEqualTo("테스트 공지사항");
        verify(noticeRepository).save(any(Notice.class));
    }

    @DisplayName("공지사항 추가 시 예외 발생 - 스터디를 찾지 못하는 경우")
    @Test
    public void createNoticeWhenNotFoundStudy() {
        // given (사전 준비)
        NoticeRequest request = NoticeRequest.builder()
            .content("New Notice Content")
            .isDeleted(false).build();

        // when (테스트 진행할 행위)
        when(studyRepository.findById(study.getId())).thenReturn(Optional.empty());

        BaseException exception = assertThrows(BaseException.class, () -> {
            noticeService.createNotice(study.getId(), request, user);
        });

        // then (행위에 대한 결과 검증)
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.STUDY_NOT_FOUND);

    }

    @DisplayName("공지사항 내용 업데이트")
    @Test
    public void testUpdateNotice() {
        // given (사전 준비)
        UpdateNoticeRequest request = new UpdateNoticeRequest();
        request.setContent("공지사항 업데이트");
        request.setDeleted(false);

        // when (테스트 진행할 행위)
        when(noticeRepository.findById(notice.getId())).thenReturn(Optional.of(notice));
        when(studyMemberRepository.findRoleByStudyIdAndUserId(study.getId(), user.getId())).thenReturn(Optional.of(RoleEnum.ROLE_LEADER));

        UpdateNoticeResponse response = noticeService.updateNotice(notice.getId(), request, user);

        // then (행위에 대한 결과 검증)
        assertThat(response.getContent()).isEqualTo("공지사항 업데이트");
        verify(noticeRepository, times(1)).findById(notice.getId());
    }

    @DisplayName("공지사항 업데이트 시 예외 발생 - 공지사항 찾지 못하는 경우")
    @Test
    public void updateNoticeWhenNotFoundNotice() {
        // given (사전 준비)
        UpdateNoticeRequest request = new UpdateNoticeRequest();
        request.setContent("공지사항 업데이트");
        request.setDeleted(false);

        // when (테스트 진행할 행위)
        when(noticeRepository.findById(notice.getId())).thenReturn(Optional.empty());

        BaseException exception = assertThrows(BaseException.class, () -> {
            noticeService.updateNotice(notice.getId(), request, user);
        });

        // then (행위에 대한 결과 검증)
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOTICE_NOT_FOUND);
    }

    @DisplayName("공지사항 삭제")
    @Test
    public void testDeleteNotice() {
        // when (테스트 진행할 행위)
        // then (행위에 대한 결과 검증)
        when(noticeRepository.findById(notice.getId())).thenReturn(Optional.of(notice));
        when(studyMemberRepository.findRoleByStudyIdAndUserId(study.getId(), user.getId())).thenReturn(Optional.of(RoleEnum.ROLE_LEADER));

        UpdateNoticeResponse response = noticeService.deleteNotice(notice.getId(), user);

        assertThat(response.isDeleted()).isTrue();
        verify(noticeRepository, times(1)).findById(notice.getId());
    }

    @DisplayName("공지사항 조회")
    @Test
    public void testGetNotice() {
        // when (테스트 진행할 행위)
        // then (행위에 대한 결과 검증)
        when(noticeRepository.findByStudyIdAndNoticeId(study.getId(), notice.getId())).thenReturn(Optional.of(notice));

        NoticeResponse response = noticeService.getNoticeByStudyIdAndNoticeId(study.getId(), notice.getId());

        assertThat(response.getContent()).isEqualTo("테스트 공지사항");
        verify(noticeRepository, times(1)).findByStudyIdAndNoticeId(study.getId(), notice.getId());
    }

    @DisplayName("공지사항 조회 시 예외 발생 - 공지사항 찾지 못하는 경우")
    @Test
    public void testGetNoticeWhenNotFoundNotice() {
        // when (테스트 진행할 행위)
        // then (행위에 대한 결과 검증)
        when(noticeRepository.findByStudyIdAndNoticeId(study.getId(), notice.getId())).thenReturn(Optional.empty());

        BaseException exception = assertThrows(BaseException.class, () -> {
            noticeService.getNoticeByStudyIdAndNoticeId(study.getId(), notice.getId());
        });

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOTICE_NOT_FOUND);
    }

}