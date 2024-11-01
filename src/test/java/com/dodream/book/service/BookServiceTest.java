//package com.dodream.book.service;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import com.dodream.book.domain.BookRequest;
//import com.dodream.book.domain.BookResponse;
//import com.dodream.book.domain.BookUpdateRequest;
//import com.dodream.book.domain.BookUpdateResponse;
//import com.dodream.book.entity.Book;
//import com.dodream.book.repository.BookCommentRepository;
//import com.dodream.book.repository.BookRepository;
//import com.dodream.book.repository.BookmarkRepository;
//import com.dodream.common.enumtype.Category;
//import com.dodream.common.exception.BaseException;
//import com.dodream.common.exception.ErrorCode;
//import com.dodream.user.entity.User;
//import com.dodream.user.repository.UserRepository;
//import java.time.LocalDateTime;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//
//@ExtendWith(MockitoExtension.class)
//class BookServiceTest {
//
//    @Mock
//    private BookRepository bookRepository;
//
//    @Mock
//    private BookmarkRepository bookmarkRepository;
//
//    @InjectMocks
//    private BookServiceImpl bookService;
//
//    @Mock
//    private BookCommentRepository bookCommentRepository;
//
//    private User user;
//    private BookRequest bookRequest;
//    private Pageable pageable;
//
//    @BeforeEach
//    void setUp() {
//        user = User.builder().id(1L).username("testuser").provider("provider1").providerId("1").build();
//        bookRequest = new BookRequest();
//        bookRequest.setTitle("Sample Book");
//        bookRequest.setCategory(Category.CATEGORY_CS);
//        bookRequest.setSecret(false);
//
//        // Pageable 설정
//        pageable = PageRequest.of(0, 12, Sort.by("createdAt").descending());
//    }
//
//    @DisplayName("문제집 조회 성공")
//    @Test
//    void testGetBooks() {
//        // given
//        Book book = Book.builder()
//            .id(1L)
//            .title("Sample Book")
//            .user(user)
//            .category(Category.CATEGORY_CS)
//            .secret(false)
//            .build();
//
//        when(bookRepository.findAllBySecretFalseOrderByCreatedAtDesc(pageable))
//            .thenReturn(new PageImpl<>(Collections.singletonList(book)));
//
//        // when
//        Page<BookResponse> responses = bookService.getBooks(null, pageable, false);
//
//        // then
//        assertThat(responses).isNotNull();
//        assertThat(responses.getContent()).hasSize(1);
//        assertThat(responses.getContent().get(0).getTitle()).isEqualTo("Sample Book");
//    }
//
//    @DisplayName("카테고리별 문제집 조회 성공")
//    @Test
//    void testGetBooksByCategory() {
//        // given
//        Book book = Book.builder()
//            .id(1L)
//            .title("Sample Book")
//            .user(user)
//            .category(Category.CATEGORY_CS)
//            .secret(false)
//            .build();
//
//        when(bookRepository.findAllByCategoryAndSecretFalseOrderByCreatedAtDesc(Category.CATEGORY_CS, pageable))
//            .thenReturn(new PageImpl<>(Collections.singletonList(book)));
//
//        // when
//        Page<BookResponse> responses = bookService.getBooks("CATEGORY_CS", pageable, false);
//
//        // then
//        assertThat(responses).isNotNull();
//        assertThat(responses.getContent()).hasSize(1);
//        assertThat(responses.getContent().get(0).getTitle()).isEqualTo("Sample Book");
//    }
//
//    @DisplayName("잘못된 카테고리 조회 시 예외 발생")
//    @Test
//    void testGetBooksByInvalidCategory() {
//        assertThrows(BaseException.class, () -> {
//            bookService.getBooks("INVALID_CATEGORY", pageable, false);
//        });
//    }
//
//    @DisplayName("문제집 제목으로 검색 성공")
//    @Test
//    void testSearchBooksByKeyword() {
//        // given
//        Book book = Book.builder()
//            .id(1L)
//            .title("Sample Book")
//            .user(user)
//            .category(Category.CATEGORY_CS)
//            .secret(false)
//            .build();
//
//        when(bookRepository.findAllByTitleContainingAndSecretFalseOrderByCreatedAtDesc("Sample", pageable))
//            .thenReturn(new PageImpl<>(Collections.singletonList(book)));
//
//        // when
//        Page<BookResponse> responses = bookService.searchBooksByKeyword("Sample", pageable);
//
//        // then
//        assertThat(responses).isNotNull();
//        assertThat(responses.getContent()).hasSize(1);
//        assertThat(responses.getContent().get(0).getTitle()).isEqualTo("Sample Book");
//    }
//
//    @DisplayName("제목으로 검색 시 결과가 없을 경우 예외 발생")
//    @Test
//    void testSearchBooksByKeyword_NoBooksFound() {
//        when(bookRepository.findAllByTitleContainingAndSecretFalseOrderByCreatedAtDesc("Nonexistent", pageable))
//            .thenReturn(new PageImpl<>(Collections.emptyList()));
//
//        assertThrows(BaseException.class, () -> {
//            bookService.searchBooksByKeyword("Nonexistent", pageable);
//        });
//    }
//
//    @DisplayName("문제집 생성 성공")
//    @Test
//    void testAddBook() {
//        // given
//        Book book = Book.builder()
//            .id(1L)
//            .title("Sample Book")
//            .user(user)
//            .category(Category.CATEGORY_CS)
//            .secret(false)
//            .build();
//
//        when(bookRepository.save(any(Book.class))).thenReturn(book);
//
//        // when
//        BookResponse response = bookService.addBook(user, bookRequest);
//
//        // then
//        assertThat(response).isNotNull();
//        assertThat(response.getTitle()).isEqualTo("Sample Book");
//        assertThat(response.getUsername()).isEqualTo("testuser");
//    }
//
//    @DisplayName("문제집 수정 성공")
//    @Test
//    void testUpdateBook() {
//        // given
//        Book existingBook = Book.builder()
//            .id(1L)
//            .title("Old Title")
//            .user(user)
//            .category(Category.CATEGORY_CS)
//            .secret(false)
//            .build();
//
//        BookUpdateRequest updateRequest = new BookUpdateRequest();
//        updateRequest.setTitle("New Title");
//        updateRequest.setCategory("CATEGORY_CS");
//
//        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
//        when(bookRepository.save(any(Book.class))).thenReturn(existingBook);
//
//        // when
//        BookUpdateResponse response = bookService.updateBook(user, 1L, updateRequest);
//
//        // then
//        assertThat(response).isNotNull();
//        assertThat(response.getTitle()).isEqualTo("New Title");
//    }
//
//    @DisplayName("문제집 수정 시 소유자 확인 실패")
//    @Test
//    void testUpdateBook_AccessDenied() {
//        // given
//        Book existingBook = Book.builder()
//            .id(1L)
//            .title("Old Title")
//            .user(User.builder().id(2L).build())
//            .category(Category.CATEGORY_CS)
//            .secret(false)
//            .build();
//
//        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
//
//        // when & then
//        BaseException exception = assertThrows(BaseException.class, () -> {
//            bookService.updateBook(user, 1L, new BookUpdateRequest());
//        });
//        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ACCESS_DENIED);
//    }
//
//    @DisplayName("문제집 삭제 성공")
//    @Test
//    void testDeleteBook() {
//        // given
//        Book existingBook = Book.builder()
//            .id(1L)
//            .title("Book to be deleted")
//            .user(user)
//            .category(Category.CATEGORY_CS)
//            .secret(false)
//            .build();
//
//        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
//
//        // when
//        bookService.deleteBook(1L, user);
//
//        // then
//        verify(bookCommentRepository).deleteByBookId(existingBook.getId()); // Comment 삭제 검증 추가
//        verify(bookRepository).delete(existingBook);
//    }
//
//    @DisplayName("문제집 삭제 시 소유자 확인 실패")
//    @Test
//    void testDeleteBook_AccessDenied() {
//        // given
//        Book existingBook = Book.builder()
//            .id(1L)
//            .title("Book to be deleted")
//            .user(User.builder().id(2L).build())
//            .category(Category.CATEGORY_CS)
//            .secret(false)
//            .build();
//
//        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
//
//        // when & then
//        BaseException exception = assertThrows(BaseException.class, () -> {
//            bookService.deleteBook(1L, user);
//        });
//        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ACCESS_DENIED);
//    }
//
//    @DisplayName("문제집 북마크 수에 따라 정렬하여 조회 성공")
//    @Test
//    void testGetBooks_SortedByBookmarks() {
//        // given
//        Book book1 = Book.builder()
//            .id(1L)
//            .title("Book with More Bookmarks")
//            .user(user)
//            .category(Category.CATEGORY_CS)
//            .secret(false)
//            .build();
//
//        Book book2 = Book.builder()
//            .id(2L)
//            .title("Book with Fewer Bookmarks")
//            .user(user)
//            .category(Category.CATEGORY_CS)
//            .secret(false)
//            .build();
//
//        // Mocking the repository to return books sorted by bookmarks
//        when(bookmarkRepository.countByBookAndIsDeletedFalse(book1)).thenReturn(10L);
//        when(bookmarkRepository.countByBookAndIsDeletedFalse(book2)).thenReturn(5L);
//        when(bookRepository.findAllBySecretFalseOrderByBookmarkCount(pageable))
//            .thenReturn(new PageImpl<>(List.of(book1, book2)));
//
//        // when
//        Page<BookResponse> responses = bookService.getBooks(null, pageable, true);
//
//        // then
//        assertThat(responses).isNotNull();
//        assertThat(responses.getContent()).hasSize(2);
//        assertThat(responses.getContent().get(0).getTitle()).isEqualTo("Book with More Bookmarks"); // 북마크가 더 많은 책
//        assertThat(responses.getContent().get(1).getTitle()).isEqualTo("Book with Fewer Bookmarks");
//    }
//
//    @DisplayName("문제집 개별 조회 성공")
//    @Test
//    void testGetBook_Success() {
//        // given
//        Book book = Book.builder()
//            .id(1L)
//            .title("Sample Book")
//            .user(user)
//            .category(Category.CATEGORY_CS)
//            .secret(false)
//            .createdAt(LocalDateTime.now())
//            .build();
//
//        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
//        when(bookmarkRepository.countByBookAndIsDeletedFalse(book)).thenReturn(0L);
//
//        // when
//        BookResponse response = bookService.getBook(1L);
//
//        // then
//        assertThat(response).isNotNull();
//        assertThat(response.getTitle()).isEqualTo("Sample Book");
//        assertThat(response.getUsername()).isEqualTo("testuser");
//    }
//
//    @DisplayName("문제집 개별 조회 시 존재하지 않는 문제집")
//    @Test
//    void testGetBook_NotFound() {
//        // given
//        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
//
//        // when & then
//        BaseException exception = assertThrows(BaseException.class, () -> {
//            bookService.getBook(1L);
//        });
//        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.BOOK_NOT_FOUND);
//    }
//
//}