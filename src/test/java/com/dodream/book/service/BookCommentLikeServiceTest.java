package com.dodream.book.service;

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

import com.dodream.book.domain.BookCommentLikeResponse;
import com.dodream.book.entity.BookComment;
import com.dodream.book.entity.BookCommentLike;
import com.dodream.book.repository.BookCommentLikeRepository;
import com.dodream.book.repository.BookCommentRepository;
import com.dodream.common.exception.BaseException;
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
public class BookCommentLikeServiceTest {

    @InjectMocks
    private BookCommentLikeServiceImpl bookCommentLikeService;

    @Mock
    private BookCommentLikeRepository bookCommentLikeRepository;

    @Mock
    private BookCommentRepository bookCommentRepository;

    private User user;
    private BookComment comment;

    @BeforeEach
    public void setUp() {
        user = User.builder().id(1L).username("testUser").build();
        comment = BookComment.builder().id(1L).build();
    }

    @DisplayName("좋아요 없을 때 좋아요 생성")
    @Test
    public void testCreateCommentLike() {
        // given (사전 준비)
        when(bookCommentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        when(bookCommentLikeRepository.findByUserAndCommentId(user, comment)).thenReturn(Optional.empty());

        // when (테스트 진행할 범위)
        BookCommentLikeResponse response = bookCommentLikeService.toggleCommentLike(user, comment.getId());

        // then (범위에 대한 결과 검증)
        assertNotNull(response);
        assertFalse(response.isDeleted());
        assertEquals(user.getId(), response.getUserId());
        assertEquals(comment.getId(), response.getCommentId());

        verify(bookCommentLikeRepository, times(1)).save(any(BookCommentLike.class));
    }

    @DisplayName("좋아요 있을 때 좋아요 토글(생성/취소)")
    @Test
    public void testToggleCommentLike() {
        // given (사전 준비)
        BookCommentLike existingLike = BookCommentLike.builder().id(1L).user(user).commentId(comment).isDeleted(false).build();
        when(bookCommentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        when(bookCommentLikeRepository.findByUserAndCommentId(user, comment)).thenReturn(Optional.of(existingLike));

        // when (테스트 진행할 범위)
        BookCommentLikeResponse response = bookCommentLikeService.toggleCommentLike(user, comment.getId());

        // then (범위에 대한 결과 검증)
        assertNotNull(response);
        assertTrue(response.isDeleted());
        assertEquals(existingLike.getId(), response.getId());
        assertEquals(user.getId(), response.getUserId());
        assertEquals(comment.getId(), response.getCommentId());

        verify(bookCommentLikeRepository, times(1)).save(any(BookCommentLike.class));
    }

    @DisplayName("존재하지 않는 댓글에 좋아요를 생성할 때")
    @Test
    public void testLikeNonExistentComment() {
        // given (사전 준비)
        when(bookCommentRepository.findById(comment.getId())).thenReturn(Optional.empty());

        // when / then (예외 발생 검증)
        assertThrows(
            BaseException.class, () -> bookCommentLikeService.toggleCommentLike(user, comment.getId()));
        verify(bookCommentLikeRepository, never()).save(any(BookCommentLike.class));
    }
}