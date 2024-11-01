package com.dodream.study.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dodream.common.exception.BaseException;
import com.dodream.common.exception.ErrorCode;
import com.dodream.study.domain.NoticeCommentRequest;
import com.dodream.study.domain.NoticeCommentResponse;
import com.dodream.study.domain.NoticeCommentUpdateRequest;
import com.dodream.study.domain.NoticeCommentUpdateResponse;
import com.dodream.study.entity.Notice;
import com.dodream.study.entity.NoticeComment;
import com.dodream.study.repository.NoticeCommentRepository;
import com.dodream.study.repository.NoticeRepository;
import com.dodream.user.entity.User;
import com.dodream.user.repository.UserRepository;
import java.util.Collections;
import java.util.Optional;
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
class NoticeCommentServiceTest {
    @Mock
    private NoticeCommentRepository noticeCommentRepository;

    @Mock
    private NoticeRepository noticeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NoticeCommentServiceImpl noticeCommentService;

    private User user;
    private Notice notice;
    private NoticeComment noticeComment;

    @BeforeEach
    public void setup() {
        user = User.builder()
            .id(1L)
            .username("testUser")
            .provider("provider1")
            .providerId("1")
            .build();
        userRepository.save(user);

        notice = Notice.builder()
            .id(1L)
            .content("Test Notice")
            .isDeleted(false)
            .build();

        noticeRepository.save(notice);

        noticeComment = NoticeComment.builder()
            .id(1L)
            .user(user)
            .content("Test NoticeComment")
            .notice(notice)
            .build();
    }

    @DisplayName("공지사항 댓글 추가")
    @Test
    public void testAddNoticeComment() {
        // given (사전 준비)
        // when (테스트 진행할 행위)
        when(noticeRepository.findById(1L)).thenReturn(Optional.of(notice));
        when(noticeCommentRepository.save(any(NoticeComment.class))).thenReturn(noticeComment);

        NoticeCommentRequest request = new NoticeCommentRequest();
        request.setContent("Test Comment");

        NoticeCommentResponse response = noticeCommentService.addNoticeComment(1L, user, request);

        // then (행위에 대한 결과 검증)
        assertNotNull(response);
        assertEquals("Test NoticeComment", response.getContent());
        assertEquals("testUser", response.getUsername());
        assertEquals(0, response.getLikeCount());
    }

    @DisplayName("좋아요 순으로 조회")
    @Test
    public void testGetNoticeCommentOrderByLikesDesc() {
        // given (사전 준비)
        Pageable pageable = PageRequest.of(0, 10);

        // when (테스트 진행할 행위)
        when(noticeRepository.existsById(1L)).thenReturn(true);
        when(noticeCommentRepository.findByNoticeIdOrderByLikeCountDesc(pageable, 1L))
            .thenReturn(new PageImpl<>(Collections.singletonList(noticeComment)));
        Page<NoticeCommentResponse> responsePage = noticeCommentService.getNoticeCommentList(
            pageable, 1L, user, false);

        // then (행위에 대한 결과 검증)
        assertNotNull(responsePage);
        assertEquals(1, responsePage.getTotalElements());
        assertEquals("Test NoticeComment", responsePage.getContent().get(0).getContent());
    }

    @DisplayName("생성일자 순으로 조회")
    @Test
    public void testGetNoticeCommentOrderByCreatedAtDesc() {
        // given (사전 준비)
        Pageable pageable = PageRequest.of(0, 10);

        // when (테스트 진행할 행위)
        when(noticeRepository.existsById(1L)).thenReturn(true);
        when(noticeCommentRepository.findByNoticeIdOrderByCreatedAtDesc(pageable, 1L))
            .thenReturn(new PageImpl<>(Collections.singletonList(noticeComment)));
        Page<NoticeCommentResponse> responsePage = noticeCommentService.getNoticeCommentList(
            pageable, 1L, user, false);

        // then (행위에 대한 결과 검증)
        assertNotNull(responsePage);
        assertEquals(1, responsePage.getTotalElements());
        assertEquals("Test NoticeComment", responsePage.getContent().get(0).getContent());
    }

    @DisplayName("공지사항 댓글 삭제")
    @Test
    public void testDeleteNoticeComment() {
        // given (사전 준비)
        // when (테스트 진행할 행위)
        when(noticeCommentRepository.findById(1L)).thenReturn(Optional.of(noticeComment));
        noticeCommentService.deleteNoticeComment(1L, user);

        // then (행위에 대한 결과 검증)
        verify(noticeCommentRepository, times(1)).delete(noticeComment);
    }

    @DisplayName("공지사항 댓글 삭제 안 됨 - 권한 없음")
    @Test
    public void testDeleteNoticeCommentAccessDenied() {
        // given (사전 준비)
        User otherUser = new User();
        otherUser.setId(2L);

        // when (테스트 진행할 행위)
        when(noticeCommentRepository.findById(1L)).thenReturn(Optional.of(noticeComment));

        // then (행위에 대한 결과 검증)
        BaseException exception = assertThrows(BaseException.class, () ->
            noticeCommentService.deleteNoticeComment(1L, otherUser));
        assertEquals(ErrorCode.ACCESS_DENIED, exception.getErrorCode());
    }

    @DisplayName("공지사항 댓글 업데이트")
    @Test
    public void testUpdateNoticeComment() {
        // given (사전 준비)
        // when (테스트 진행할 행위)
        when(noticeCommentRepository.findById(1L)).thenReturn(Optional.of(noticeComment));
        NoticeCommentUpdateRequest updateRequest = new NoticeCommentUpdateRequest();
        updateRequest.setContent("공지사항 댓글 업데이트 완료");
        NoticeCommentUpdateResponse response =
            noticeCommentService.updateNoticeComment(1L, user, updateRequest);

        // then (행위에 대한 결과 검증)
        assertNotNull(response);
        assertEquals("공지사항 댓글 업데이트 완료", response.getContent());
    }

    @DisplayName("공지사항 댓글 업데이트 안 됨 - 권한 없음")
    @Test
    public void testUpdateNoticeCommentAccessDenied() {
        // given (사전 준비)
        User otherUser = new User();
        otherUser.setId(2L);

        // when (테스트 진행할 행위)
        when(noticeCommentRepository.findById(1L)).thenReturn(Optional.of(noticeComment));
        NoticeCommentUpdateRequest updateRequest = new NoticeCommentUpdateRequest();
        updateRequest.setContent("공지사항 댓글 업데이트 완료");

        BaseException exception = assertThrows(BaseException.class, () ->
            noticeCommentService.updateNoticeComment(1L, otherUser, updateRequest));

        // then (행위에 대한 결과 검증)
        assertEquals(ErrorCode.ACCESS_DENIED, exception.getErrorCode());
    }

}