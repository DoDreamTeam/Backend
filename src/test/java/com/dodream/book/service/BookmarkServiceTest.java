package com.dodream.book.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.dodream.book.domain.BookmarkResponse;
import com.dodream.book.entity.Book;
import com.dodream.book.entity.Bookmark;
import com.dodream.book.repository.BookRepository;
import com.dodream.book.repository.BookmarkRepository;
import com.dodream.common.exception.BaseException;
import com.dodream.common.exception.ErrorCode;
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
class BookmarkServiceTest {

    @InjectMocks
    private BookmarkServiceImpl bookmarkService;

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private BookRepository bookRepository;

    private User user;
    private Book book;

    @BeforeEach
    void setUp() {
        user = User.builder()
            .id(1L)
            .username("testUser")
            .provider("provider1")
            .providerId("1")
            .build();
        book = Book.builder()
            .id(1L)
            .title("Test Book")
            .build();
    }

    @DisplayName("북마크 없을 때, 새로운 북마크 생성")
    @Test
    public void testCreateBookmark() {
        // given (사전 준비)
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookmarkRepository.findByUserAndBook(user, book)).thenReturn(Optional.empty());
        when(bookmarkRepository.save(any(Bookmark.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when (테스트 진행할 범위)
        BookmarkResponse response = bookmarkService.toggleBookmark(user, 1L);

        // then (범위에 대한 결과 검증)
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(user.getId());
        assertThat(response.getBookId()).isEqualTo(book.getId());
        assertThat(response.isDeleted()).isFalse(); // 기본값이 false인지 확인
    }

    @DisplayName("북마크 있을 때, 북마크 토글")
    @Test
    public void testToggleBookmark() {
        // given (사전 준비)
        Bookmark existingBookmark = Bookmark.builder()
            .id(1L)
            .user(user)
            .book(book)
            .isDeleted(false) // 기존 북마크는 삭제되지 않은 상태
            .build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookmarkRepository.findByUserAndBook(user, book)).thenReturn(Optional.of(existingBookmark));
        when(bookmarkRepository.save(any(Bookmark.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when (테스트 진행할 범위)
        BookmarkResponse response = bookmarkService.toggleBookmark(user, 1L);

        // then (범위에 대한 결과 검증)
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(user.getId());
        assertThat(response.getBookId()).isEqualTo(book.getId());
        assertThat(response.isDeleted()).isTrue(); // 삭제된 상태로 변경되었는지 확인
    }

    @DisplayName("존재하지 않는 책에 대한 북마크 시도")
    @Test
    public void testBookmarkNonExistentBook() {
        // given (사전 준비)
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then (테스트 진행 및 결과 검증)
        BaseException exception = assertThrows(BaseException.class, () -> bookmarkService.toggleBookmark(user, 1L));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.BOOK_NOT_FOUND);
    }
}
