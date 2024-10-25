package com.dodream.book.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.dodream.book.entity.Book;
import com.dodream.common.enumtype.Category;
import com.dodream.user.entity.User;
import com.dodream.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private List<Book> books;

    @BeforeEach
    public void setUp() {
        user = User.builder().username("hello").provider("provider1").providerId("1").build();
        userRepository.save(user); // 사용자 저장

        LocalDateTime now = LocalDateTime.now();

        books = List.of(
            Book.builder().title("Test Book1").user(user).category(Category.CATEGORY_CS)
                .secret(false).createdAt(now.minusDays(4)).build(), // 4일 전
            Book.builder().title("Test Book2").user(user).category(Category.CATEGORY_ETC)
                .secret(false).createdAt(now.minusDays(3)).build(), // 3일 전
            Book.builder().title("Test Book3").user(user).category(Category.CATEGORY_CS)
                .secret(false).createdAt(now.minusDays(2)).build(), // 2일 전
            Book.builder().title("Test Book4").user(user).category(Category.CATEGORY_CERT)
                .secret(false).createdAt(now.minusDays(1)).build(), // 1일 전
            Book.builder().title("Test Book5").user(user).category(Category.CATEGORY_ETC)
                .secret(true).createdAt(now).build(), // 오늘
            Book.builder().title("Test Book6").user(user).category(Category.CATEGORY_CERT)
                .secret(false).createdAt(now.plusDays(1)).build() // 내일
        );


        bookRepository.saveAll(books); // 모든 책 저장
    }

    @DisplayName("전체 문제집 최신순 조회 - 페이지네이션")
    @Test
    public void getAllBooksWithPaginationTest() {
        // given (사전 준비)
        PageRequest pageable = PageRequest.of(0, 3, Sort.by("createdAt").descending());

        // when
        Page<Book> resultBooks = bookRepository.findAllBySecretFalseOrderByCreatedAtDesc(pageable);

        // then
        assertThat(resultBooks).isNotNull();
        assertThat(resultBooks.getContent()).isNotEmpty();
        assertThat(resultBooks.getTotalElements()).isEqualTo(5); // 공개 문제집 5개
        assertThat(resultBooks.getTotalPages()).isEqualTo(2); // 2 페이지
        assertThat(resultBooks.getContent().size()).isEqualTo(3); // 첫 페이지는 3개
        assertThat(resultBooks.getContent().get(0).getTitle()).isEqualTo("Test Book6"); // 최신순 검증
    }

    @DisplayName("특정 카테고리별로 문제집 최신순 조회 - 페이지네이션")
    @Test
    public void getCategoryBooksWithPaginationTest() {
        // given (사전 준비)
        PageRequest pageable = PageRequest.of(0, 1, Sort.by("createdAt").descending());

        // when
        Page<Book> resultBooks = bookRepository.findAllByCategoryAndSecretFalseOrderByCreatedAtDesc(
            Category.CATEGORY_CS, pageable);

        // then
        assertThat(resultBooks).isNotNull();
        assertThat(resultBooks.getContent()).isNotEmpty();
        assertThat(resultBooks.getTotalElements()).isEqualTo(2); // 공개 문제집 2개
        assertThat(resultBooks.getContent().get(0).getTitle()).isEqualTo("Test Book3"); // 최신순 검증
        assertThat(resultBooks.getTotalPages()).isEqualTo(2); // 2 페이지
    }

    @DisplayName("문제집 제목으로 검색 - 페이지네이션")
    @Test
    public void searchByBookTitleWithPaginationTest() {
        // given (사전 준비)
        PageRequest pageable = PageRequest.of(0, 1, Sort.by("createdAt").descending());

        // when
        Page<Book> resultBooks = bookRepository.findAllByTitleContainingAndSecretFalseOrderByCreatedAtDesc(
            "Book", pageable);

        // then
        assertThat(resultBooks).isNotNull();
        assertThat(resultBooks.getContent()).isNotEmpty();
        assertThat(resultBooks.getContent().size()).isEqualTo(1); // 제목에 "Book"이 포함된 문제집 1개
    }

    @DisplayName("문제집 생성하기")
    @Test
    public void addBookTest() {
        // given (사전 준비)
        user = User.builder().username("hello").provider("provider1").providerId("1").build();
        userRepository.save(user); // 사용자 저장

        Book book = Book
            .builder()
            .title("add test book")
            .user(user)
            .category(Category.CATEGORY_CERT)
            .secret(false)
            .build();

        // when (테스트 진행할 범위)
        Book savedBook = bookRepository.save(book);

        // then (범위에 대한 결과 검증)
        assertThat(savedBook).isNotNull(); // null 이 아닌가?
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("add test book");
        assertThat(savedBook.getUser().getUsername()).isEqualTo("hello");
        assertThat(savedBook.getCategory()).isEqualTo(Category.CATEGORY_CERT);
    }

    @DisplayName("문제집 삭제하기")
    @Test
    public void deleteBookTest() {
        // given (사전 준비)
        // setUp() 참고
        Long bookId = 1L;

        // when (테스트 진행할 범위)
        bookRepository.deleteById(bookId);

        // then (범위에 대한 결과 검증)
        Optional<Book> findBook = bookRepository.findById(bookId);
        Assertions.assertThat(findBook).isNotPresent();

    }

}