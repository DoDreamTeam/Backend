package com.dodream.book.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.dodream.book.domain.BookRequest;
import com.dodream.book.domain.BookResponse;
import com.dodream.book.entity.Book;
import com.dodream.book.repository.BookRepository;
import com.dodream.book.repository.BookmarkRepository;
import com.dodream.common.enumtype.Category;
import com.dodream.common.exception.BaseException;
import com.dodream.common.exception.ErrorCode;
import com.dodream.user.entity.User;
import com.dodream.user.repository.UserRepository;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private User user;
    private BookRequest bookRequest;

    @BeforeEach
    void setUp() {
        user = User.builder().username("hello").provider("provider1").providerId("1").build();

        bookRequest = new BookRequest();
        bookRequest.setTitle("title");
        bookRequest.setCategory(Category.CATEGORY_CS);
        bookRequest.setSecret(false);
    }

    @DisplayName("전체 문제집 조회 성공")
    @Test
    public void testGetAllBooks() {
        // given (사전 준비)
        Book book = Book
            .builder()
            .title("Test Book")
            .user(user)
            .category(Category.CATEGORY_CS)
            .secret(false)
            .build();

        // when (테스트 진행할 범위)
        when(bookRepository.findAllBySecretFalseOrderByCreatedAtDesc()).thenReturn(Collections.singletonList(book));
        List<BookResponse> responses = bookService.getBookList();

        // then (범위에 대한 결과 검증)
        assertThat(responses).isNotNull(); // null이 아닌가?
        assertThat(responses).isNotEmpty(); // 비어있는가?
        assertThat(responses.size()).isEqualTo(1); // 하나의 문제집이 조회되었는가?
        assertThat(responses.get(0).getTitle()).isEqualTo("Test Book"); // 제목이 맞는가?
    }

    @DisplayName("카테고리별 문제집 조회 성공")
    @Test
    public void testGetBooksByCategory() {
        // given (사전 준비)
        Book book = Book
            .builder()
            .title("Test Book")
            .user(user)
            .category(Category.CATEGORY_CS)
            .secret(false)
            .build();

        // when (테스트 진행할 범위)
        when(bookRepository.findAllByCategoryAndSecretFalseOrderByCreatedAtDesc(Category.CATEGORY_CS)).thenReturn(
            Collections.singletonList(book));
        List<BookResponse> responses = bookService.getBookListByCategory("CATEGORY_CS");

        // then (범위에 대한 결과 검증)
        assertThat(responses).isNotNull(); // null이 아닌가?
        assertThat(responses).isNotEmpty(); // 비어있는가?
        assertThat(responses.size()).isEqualTo(1); // 하나의 문제집이 조회되었는가?
        assertThat(responses.get(0).getTitle()).isEqualTo("Test Book"); // 제목이 맞는가?
    }

    @DisplayName("잘못된 카테고리 예외 처리")
    @Test
    public void testGetBooksByInvalidCategory() {
        assertThrows(BaseException.class, () -> bookService.getBookListByCategory("InvalidCategory"));
    }

    @DisplayName("제목으로 문제집 검색하기 성공")
    @Test
    public void testSearchBooksByTitle() {
        // given (사전 준비)
        Book book = Book
            .builder()
            .title("Test Book")
            .user(user)
            .category(Category.CATEGORY_CS)
            .secret(false)
            .build();

        // when (테스트 진행할 범위)
        when(bookRepository.findAllByTitleContainingAndSecretFalseOrderByCreatedAtDesc("Test")).thenReturn(
            Collections.singletonList(book));

        List<BookResponse> responses = bookService.searchBooksByKeyword("Test");

        // then (범위에 대한 결과 검증)
        assertThat(responses).isNotNull(); // null이 아닌가?
        assertThat(responses).isNotEmpty(); // 비어있는가?
        assertThat(responses.size()).isEqualTo(1); // 하나의 문제집이 조회되었는가?
        assertThat(responses.get(0).getTitle()).isEqualTo("Test Book"); // 제목이 맞는가?
    }

    @DisplayName("제목으로 문제집 검색 결과가 없을 경우 예외 처리")
    @Test
    public void testSearchBooksByTitle_NoBooksFound() {
        when(bookRepository.findAllByTitleContainingAndSecretFalseOrderByCreatedAtDesc("NonExistent")).thenReturn(Collections.emptyList());

        BaseException exception = assertThrows(BaseException.class, () -> bookService.searchBooksByKeyword("NonExistent"));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.BOOK_SEARCH_NOT_FOUND);
    }

    @DisplayName("문제집 생성 성공")
    @Test
    public void testAddBook() {
        // given (사전 준비)
        Book book = Book
            .builder()
            .title("Test Book")
            .user(user)
            .category(Category.CATEGORY_CS)
            .secret(false)
            .build();

        // when (테스트 진행할 범위)
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        BookResponse response = bookService.addBook(user, bookRequest);

        // then (범위에 대한 결과 검증)
        assertThat(response).isNotNull(); // null이 아닌가?
        assertThat(response.getTitle()).isEqualTo("Test Book"); // 제목이 맞는가?
        assertThat(response.getUsername()).isEqualTo("hello"); // 사용자 이름이 맞는가?
        assertThat(response.getCategory()).isEqualTo("CATEGORY_CS"); // 카테고리가 맞는가?
    }
}