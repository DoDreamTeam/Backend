package com.dodream.book.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.dodream.book.entity.Book;
import com.dodream.common.enumtype.Category;
import com.dodream.user.entity.User;
import com.dodream.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
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


    @DisplayName("전체 문제집 조회 테스트")
    @Transactional
    @Test
    public void getAllBooksTest() {
        // given (사전 준비)
        User user1 = User.builder().id(1L).username("hello").provider("provider1").providerId("1").build();
        userRepository.save(user1); // 사용자 저장

        Book book1 = Book.builder()
            .title("Test Book1")
            .user(user1)
            .category(Category.CATEGORY_CS)
            .secret(false)
            .build(); // 공개 문제집
        bookRepository.save(book1);

        Book book2 = Book.builder()
            .title("Test Book1")
            .user(user1)
            .category(Category.CATEGORY_CS)
            .secret(false)
            .build(); // 공개 문제집
        bookRepository.save(book2);

        Book book3 = Book.builder()
            .title("Test Book3")
            .user(user1)
            .category(Category.CATEGORY_CS)
            .secret(false)
            .build(); // 공개 문제집
        bookRepository.save(book3);

        Book book4 = Book.builder()
            .title("Test Book4")
            .user(user1)
            .category(Category.CATEGORY_CS)
            .secret(false)
            .build(); // 공개 문제집
        bookRepository.save(book4);

        Book book5 = Book.builder()
            .title("Test Book5")
            .user(user1)
            .category(Category.CATEGORY_CS)
            .secret(true)
            .build(); // 비공개 문제집
        bookRepository.save(book5);


        // when (테스트 진행할 범위)
        List<Book> books = bookRepository.findAllBySecretFalse();

        // then (범위에 대한 결과 검증)
        assertThat(books).hasSize(4); // 공개 문제집 4개
        assertThat(books.get(0).getTitle()).isEqualTo("Test Book1"); // 첫 번째 제목 검증
    }

}