package com.dodream.book.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dodream.book.domain.BookCommentRequest;
import com.dodream.book.domain.BookCommentResponse;
import com.dodream.book.domain.BookCommentUpdateRequest;
import com.dodream.book.domain.BookCommentUpdateResponse;
import com.dodream.book.entity.Book;
import com.dodream.book.entity.BookComment;
import com.dodream.book.repository.BookCommentLikeRepository;
import com.dodream.book.repository.BookCommentRepository;
import com.dodream.book.repository.BookRepository;
import com.dodream.common.enumtype.Category;
import com.dodream.common.exception.BaseException;
import com.dodream.common.exception.ErrorCode;
import com.dodream.user.entity.User;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class BookCommentServiceTest {

    @InjectMocks
    private BookCommentServiceImpl bookCommentService;

    @Mock
    private BookCommentRepository bookCommentRepository;

    @Mock
    private BookCommentLikeRepository bookCommentLikeRepository;

    @Mock
    private BookRepository bookRepository;

    private User user;
    private Book book;

    @BeforeEach
    void setUp() {
        user = User.builder()
            .id(1L)
            .username("testUser")
            .build();

        book = Book.builder()
            .id(1L)
            .title("Test Book")
            .user(user)
            .category(Category.CATEGORY_CS)
            .secret(false)
            .createdAt(LocalDateTime.now().minusDays(1))
            .build();
    }

    @DisplayName("문제집 전체 댓글 조회 성공")
    @Test
    void getCommentList_ShouldReturnPageOfBookCommentResponse() {
        // Given
        BookComment comment1 = BookComment.builder()
            .id(1L)
            .comment("comment 1")
            .user(user)
            .book(book)
            .createdAt(LocalDateTime.now().minusDays(2))
            .build();

        BookComment comment2 = BookComment.builder()
            .id(2L)
            .comment("comment 2")
            .user(user)
            .book(book)
            .createdAt(LocalDateTime.now())
            .build();

        // Mocking the repository responses
        when(bookRepository.existsById(book.getId())).thenReturn(true);
        when(bookCommentRepository.findByBookIdOrderByCreatedAtDesc(any(Pageable.class), eq(book.getId())))
            .thenReturn(new PageImpl<>(Arrays.asList(comment1, comment2))); // Ensure both comments are returned
        when(bookCommentLikeRepository.countByCommentIdAndIsDeletedFalse(comment1)).thenReturn(5L);
        when(bookCommentLikeRepository.countByCommentIdAndIsDeletedFalse(comment2)).thenReturn(10L);

        Pageable pageable = PageRequest.of(0, 5);
        boolean isSortByLikes = false;

        // When
        Page<BookCommentResponse> commentsPage = bookCommentService.getCommentList(pageable, book.getId(), isSortByLikes);

        // Then
        assertEquals(2, commentsPage.getTotalElements()); // Check total number of elements
        assertEquals(2, commentsPage.getContent().size()); // Check content size
        assertEquals(comment1.getId(), commentsPage.getContent().get(0).getId()); // Check for latest comment
        assertEquals(comment2.getId(), commentsPage.getContent().get(1).getId()); // Ensure first comment is correct
    }

    @DisplayName("문제집 ID가 존재하지 않을 때")
    @Test
    void getCommentList_ShouldThrowBaseException_WhenBookIdNotFound() {
        // Given
        Long nonExistentBookId = 999L;
        when(bookRepository.existsById(nonExistentBookId)).thenReturn(false);

        // When & Then
        BaseException exception = assertThrows(BaseException.class, () -> {
            bookCommentService.getCommentList(PageRequest.of(0, 5), nonExistentBookId, false);
        });

        assertEquals(ErrorCode.BOOK_ID_NOT_FOUND, exception.getErrorCode());
    }

    @DisplayName("문제집에 댓글 생성 성공")
    @Test
    void addComment_ShouldReturnBookCommentResponse() {
        // Given
        BookCommentRequest commentRequest = new BookCommentRequest();
        commentRequest.setComment("This is a comment.");

        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(bookCommentRepository.save(any(BookComment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        BookCommentResponse response = bookCommentService.addComment(book.getId(), user, commentRequest);

        // Then
        assertEquals("This is a comment.", response.getComment());
        assertEquals("testUser", response.getUsername());
        assertEquals(book.getId(), response.getBookId());
        assertEquals(0L, response.getLikeCount());
    }

    @DisplayName("존재하지 않는 문제집에 댓글 추가 시 예외 발생")
    @Test
    void addComment_WhenBookNotFound_ShouldThrowException() {
        // Given
        BookCommentRequest commentRequest = new BookCommentRequest();
        commentRequest.setComment("This is a comment.");

        when(bookRepository.findById(book.getId())).thenReturn(Optional.empty());

        // When & Then
        BaseException exception = assertThrows(BaseException.class, () ->
            bookCommentService.addComment(book.getId(), user, commentRequest));

        assertEquals(ErrorCode.BOOK_NOT_FOUND, exception.getErrorCode());
    }

    @DisplayName("문제집 댓글 수정 성공")
    @Test
    void updateComment_ShouldReturnUpdatedResponse() {
        // Given
        BookComment existingComment = BookComment.builder()
            .id(1L)
            .comment("Existing comment")
            .user(user)
            .book(book)
            .createdAt(LocalDateTime.now())
            .build();

        BookCommentUpdateRequest updateRequest = new BookCommentUpdateRequest();
        updateRequest.setComment("Updated comment");

        when(bookCommentRepository.findById(anyLong())).thenReturn(Optional.of(existingComment));

        // When
        BookCommentUpdateResponse response = bookCommentService.updateComment(1L, user, updateRequest);

        // Then
        assertEquals("Updated comment", response.getComment());
        assertEquals("Updated comment", existingComment.getComment()); // Ensure the comment is updated
        verify(bookCommentRepository).findById(1L);
    }

    @DisplayName("댓글 수정 시 댓글이 존재하지 않을 때 예외 발생")
    @Test
    void updateComment_WhenCommentNotFound_ShouldThrowException() {
        // Given
        BookCommentUpdateRequest updateRequest = new BookCommentUpdateRequest();
        updateRequest.setComment("Updated comment");

        when(bookCommentRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        BaseException exception = assertThrows(BaseException.class, () -> {
            bookCommentService.updateComment(1L, user, updateRequest);
        });

        assertEquals(ErrorCode.BOOK_COMMENT_NOT_FOUND, exception.getErrorCode());
    }

    @DisplayName("댓글 수정 시 작성자가 다를 때 예외 발생")
    @Test
    void updateComment_WhenUserIsNotAuthor_ShouldThrowException() {
        // Given
        User differentUser = User.builder().id(2L).username("differentUser").build();
        BookComment existingComment = BookComment.builder()
            .id(1L)
            .comment("Existing comment")
            .user(differentUser)
            .book(book)
            .createdAt(LocalDateTime.now())
            .build();

        BookCommentUpdateRequest updateRequest = new BookCommentUpdateRequest();
        updateRequest.setComment("Updated comment");

        when(bookCommentRepository.findById(anyLong())).thenReturn(Optional.of(existingComment));

        // When & Then
        BaseException exception = assertThrows(BaseException.class, () -> {
            bookCommentService.updateComment(1L, user, updateRequest);
        });

        assertEquals(ErrorCode.ACCESS_DENIED, exception.getErrorCode());
    }

    @DisplayName("문제집 댓글 삭제 성공")
    @Test
    void deleteComment_ShouldSucceed() {
        // Given
        BookComment existingComment = BookComment.builder()
            .id(1L)
            .comment("Existing comment")
            .user(user)
            .book(book)
            .createdAt(LocalDateTime.now())
            .build();

        when(bookCommentRepository.findById(anyLong())).thenReturn(Optional.of(existingComment));

        // When
        bookCommentService.deleteComment(1L, user);

        // Then
        verify(bookCommentRepository).delete(existingComment);
    }

    @DisplayName("댓글 삭제 시 댓글이 존재하지 않을 때 예외 발생")
    @Test
    void deleteComment_WhenCommentNotFound_ShouldThrowException() {
        // Given
        when(bookCommentRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        BaseException exception = assertThrows(BaseException.class, () -> {
            bookCommentService.deleteComment(1L, user);
        });

        assertEquals(ErrorCode.BOOK_COMMENT_NOT_FOUND, exception.getErrorCode());
    }

    @DisplayName("댓글 삭제 시 작성자가 다를 때 예외 발생")
    @Test
    void deleteComment_WhenUserIsNotAuthor_ShouldThrowException() {
        // Given
        User differentUser = User.builder().id(2L).username("differentUser").build();
        BookComment existingComment = BookComment.builder()
            .id(1L)
            .comment("Existing comment")
            .user(differentUser)
            .book(book)
            .createdAt(LocalDateTime.now())
            .build();

        when(bookCommentRepository.findById(anyLong())).thenReturn(Optional.of(existingComment));

        // When & Then
        BaseException exception = assertThrows(BaseException.class, () -> {
            bookCommentService.deleteComment(1L, user);
        });

        assertEquals(ErrorCode.ACCESS_DENIED, exception.getErrorCode());
    }
}


