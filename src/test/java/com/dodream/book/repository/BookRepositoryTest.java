package com.dodream.book.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.dodream.book.entity.Book;
import com.dodream.common.enumtype.Category;
import com.dodream.user.entity.User;
import com.dodream.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

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
        // 전체 문제집 조회에 필요한 문제집 리스트 미리 생성하기
        user = User.builder().username("hello").provider("provider1").providerId("1").build();
        userRepository.save(user); // 사용자 저장

        books = List.of(
            Book
                .builder()
                .title("Test Book1")
                .user(user)
                .category(Category.CATEGORY_CS)
                .secret(false)
                .build(),
            Book
                .builder()
                .title("Test Book2")
                .user(user)
                .category(Category.CATEGORY_ETC)
                .secret(false)
                .build(),
            Book
                .builder()
                .title("Test Book3")
                .user(user)
                .category(Category.CATEGORY_CS)
                .secret(false)
                .build(),
            Book
                .builder()
                .title("Test Book4")
                .user(user)
                .category(Category.CATEGORY_CERT)
                .secret(false)
                .build(),
            Book
                .builder()
                .title("Test Book5")
                .user(user)
                .category(Category.CATEGORY_ETC)
                .secret(true)
                .build(), // 비공개 문제집
            Book
                .builder()
                .title("Test Book6")
                .user(user)
                .category(Category.CATEGORY_CERT)
                .secret(false)
                .build()
        );

        books.forEach(bookRepository::save); // 모든 책 저장
    }


    @DisplayName("전체 문제집 조회")
    @Test
    public void getAllBooksTest() {
        // given (사전 준비)
        // setUp() 참고

        // when
        List<Book> resultBooks = bookRepository.findAllBySecretFalse();

        // then
        assertThat(resultBooks).isNotNull(); // null 이 아닌가?
        assertThat(resultBooks).isNotEmpty(); // 비어있는가?
        assertThat(
            resultBooks.stream().anyMatch(book -> book.getTitle().equals("Test Book2"))).isTrue(); // Test Book2 문제집이 존재하는가?
        assertThat(resultBooks).hasSize(5); // 공개 문제집 5개
    }

    @DisplayName("특정 카테고리별로 문제집 전체 조회")
    @Test
    public void getCategoryBooksTest() {
        // given (사전 준비)
        // setUp() 참고

        // when (테스트 진행할 범위)
        List<Book> resultBooks = bookRepository.findAllByCategoryAndSecretFalse(Category.CATEGORY_ETC);

        // then (범위에 대한 결과 검증)
        assertThat(resultBooks).isNotNull(); // null 이 아닌가?
        assertThat(resultBooks).isNotEmpty(); // 비어있는가?
        assertThat(
            resultBooks.stream().anyMatch(book -> book.getTitle().equals("Test Book2"))).isTrue(); // Test Book2 문제집이 존재하는가?
        assertThat(resultBooks).hasSize(1); // ETC 인 문제집 조회 (하나는 비공개이므로 하나만 조회된다)
    }

}