package com.dodream.book.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.dodream.book.domain.BookCommentResponse;
import com.dodream.book.entity.Book;
import com.dodream.book.entity.BookComment;
import com.dodream.book.repository.BookCommentLikeRepository;
import com.dodream.book.repository.BookCommentRepository;
import com.dodream.common.enumtype.Category;
import com.dodream.common.exception.BaseException;
import com.dodream.common.exception.ErrorCode;
import com.dodream.user.entity.User;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

class BookCommentServiceTest {

    @InjectMocks
    private BookCommentServiceImpl bookCommentService;

    @Mock
    private BookCommentRepository bookCommentRepository;

    @Mock
    private BookCommentLikeRepository bookCommentLikeRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("문제집 전체 댓글 조회 성공")
    @Test
    void getCommentList_ShouldReturnListOfBookCommentResponse() {
        // Given
        User user1 = User
            .builder()
            .username("hello")
            .provider("provider1")
            .providerId("1")
            .build();

        User user2 = User
            .builder()
            .username("hello2")
            .provider("provider1")
            .providerId("2")
            .build();

        Book book = Book
            .builder()
            .id(1L)
            .title("Test Book1")
            .user(user1)
            .category(Category.CATEGORY_CS)
            .secret(false)
            .createdAt(LocalDateTime.now().minusDays(1))
            .build();

        BookComment comment1 = BookComment
            .builder()
            .comment("comment 1")
            .user(user1)
            .book(book)
            .createdAt(LocalDateTime.now().minusDays(1))
            .build();

        BookComment comment2 = BookComment
            .builder()
            .comment("comment 2")
            .user(user2)
            .book(book)
            .createdAt(LocalDateTime.now())
            .build();


        when(bookCommentRepository.findByBookIdOrderByCreatedAtDesc(book.getId())).thenReturn(Arrays.asList(comment1, comment2));
        when(bookCommentLikeRepository.countByCommentId(comment1)).thenReturn(5L);
        when(bookCommentLikeRepository.countByCommentId(comment2)).thenReturn(10L);

        // When
        List<BookCommentResponse> comments = bookCommentService.getCommentList(book.getId());

        // Then
        assertEquals(2, comments.size());
        // 최신 댓글이 첫 번째로 오도록 검증
        assertEquals(comment2.getId(), comments.get(0).getId());
        assertEquals(comment1.getId(), comments.get(1).getId());
    }

    @DisplayName("문제집 ID가 존재하지 않을 때")
    @Test
    void getCommentList_ShouldThrowBaseException_WhenBookIdNotFound() {
        // Given
        Long nonExistentBookId = 999L; // 존재하지 않는 ID
        when(bookCommentRepository.findByBookIdOrderByCreatedAtDesc(nonExistentBookId)).thenReturn(List.of());

        // When & Then
        BaseException exception = assertThrows(BaseException.class, () -> {
            bookCommentService.getCommentList(nonExistentBookId);
        });

        assertEquals(ErrorCode.BOOK_ID_NOT_FOUND, exception.getErrorCode());
    }
}
