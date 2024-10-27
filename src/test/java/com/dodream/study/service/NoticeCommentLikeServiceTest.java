package com.dodream.study.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dodream.common.exception.BaseException;
import com.dodream.common.exception.ErrorCode;
import com.dodream.study.domain.NoticeCommentLikeResponse;
import com.dodream.study.entity.NoticeComment;
import com.dodream.study.entity.NoticeCommentLike;
import com.dodream.study.repository.NoticeCommentLikeRepository;
import com.dodream.study.repository.NoticeCommentRepository;
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
class NoticeCommentLikeServiceTest {

    @Mock
    private NoticeCommentLikeRepository noticeCommentLikeRepository;

    @Mock
    private NoticeCommentRepository noticeCommentRepository;

    @InjectMocks
    private NoticeCommentLikeServiceImpl noticeCommentLikeService;

    private User user;

    private NoticeComment noticeComment;


    @BeforeEach
    void setUp() {
        user = User.builder()
            .username("testUser")
            .provider("provider1")
            .providerId("1")
            .build();

        noticeComment = NoticeComment.builder()
            .id(1L)
            .content("Test comment")
            .user(user)
            .build();
    }

    @DisplayName("공지사항 댓글 좋아요 클릭 - 좋아요 없을 때 좋아요 생성")
    @Test
    public void testToggleNoticeCommentLike() {
        // given (사전 준비)
        when(noticeCommentRepository.findById(1L)).thenReturn(Optional.of(noticeComment));
        when(noticeCommentLikeRepository.findByUserAndNoticeCommentId(user, noticeComment))
            .thenReturn(Optional.empty());

        // when (테스트 진행할 행위)
        NoticeCommentLikeResponse response = noticeCommentLikeService.toggleNoticeCommentLike(user, 1L);

        // then (행위에 대한 결과 검증)
        assertNotNull(response);
        assertEquals(user.getId(), response.getUserId());
        assertEquals(noticeComment.getId(), response.getNoticeCommentId());
        assertFalse(response.isDeleted());

        verify(noticeCommentRepository).findById(1L);
        verify(noticeCommentLikeRepository).findByUserAndNoticeCommentId(user, noticeComment);
        verify(noticeCommentLikeRepository, times(1)).save(any(NoticeCommentLike.class));
    }

    @DisplayName("좋아요 있을 때 좋아요 토글(생성/취소)")
    @Test
    public void testToggleNoticeCommentLikeAlreadyExists() {
        // given (사전 준비)
        NoticeCommentLike existingLike = NoticeCommentLike.builder()
            .id(1L)
            .user(user)
            .noticeCommentId(noticeComment)
            .isDeleted(false)
            .build();

        when(noticeCommentRepository.findById(1L)).thenReturn(Optional.of(noticeComment));
        when(noticeCommentLikeRepository.findByUserAndNoticeCommentId(user, noticeComment))
            .thenReturn(Optional.of(existingLike));

        // when (테스트 진행할 행위)
        NoticeCommentLikeResponse response = noticeCommentLikeService.toggleNoticeCommentLike(user, 1L);

        // then (행위에 대한 결과 검증)
        assertNotNull(response);
        assertEquals(user.getId(), response.getUserId());
        assertEquals(noticeComment.getId(), response.getNoticeCommentId());
        assertTrue(response.isDeleted());

        verify(noticeCommentRepository).findById(1L);
        verify(noticeCommentLikeRepository).findByUserAndNoticeCommentId(user, noticeComment);
        verify(noticeCommentLikeRepository, times(1)).save(any(NoticeCommentLike.class));
    }

    @DisplayName("존재하지 않는 댓글에 좋아요를 생성할 때 ")
    @Test
    public void testLikeNonExistNoticeComment() {
        // given (사전 준비)
        when(noticeCommentRepository.findById(1L)).thenReturn(Optional.empty());

        // when (테스트 진행할 행위)
        BaseException exception = assertThrows(BaseException.class, () ->
            noticeCommentLikeService.toggleNoticeCommentLike(user, 1L)
        );

        // then (행위에 대한 결과 검증)
        assertEquals(ErrorCode.NOTICE_COMMENT_NOT_FOUND, exception.getErrorCode());
        verify(noticeCommentRepository).findById(1L);
        verify(noticeCommentLikeRepository, never()).findByUserAndNoticeCommentId(any(), any());
        verify(noticeCommentLikeRepository, never()).save(any());
    }


}