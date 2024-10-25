package com.dodream.book.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.dodream.book.entity.Book;
import com.dodream.book.entity.Question;
import com.dodream.common.enumtype.Category;
import com.dodream.user.entity.User;
import com.dodream.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
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
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class QuestionRepositoryTest {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    private Book book;

    @BeforeEach
    void setUp() {
        User user = User.builder().username("hello").provider("provider1").providerId("1").build();
        userRepository.save(user); // 사용자 저장

        book = Book
            .builder()
            .title("add test book")
            .user(user)
            .category(Category.CATEGORY_CERT)
            .secret(false)
            .build();
        bookRepository.save(book);
    }

    @DisplayName("최신순으로 전체 문제 조회 성공")
    @Test
    void findByBookIdOrderByCreatedAtDesc() {
        // Given: 샘플 Question 엔티티 생성 및 저장
        Question question1 = Question.builder()
            .question("Question 1")
            .modelAnswer("Answer 1")
            .book(book)
            .createdAt(LocalDateTime.now().minusDays(1))
            .build();
        Question question2 = Question.builder()
            .question("Question 2")
            .modelAnswer("Answer 2")
            .book(book)
            .createdAt(LocalDateTime.now())
            .build();
        questionRepository.save(question1);
        questionRepository.save(question2);

        Pageable pageable = PageRequest.of(0, 5); // 첫 페이지, 페이지당 5개

        // When: 지정한 bookId로 최신순으로 문제 조회
        Page<Question> questionPage = questionRepository.findByBookIdOrderByCreatedAtDesc(pageable, book.getId());

        // Then: 조회된 질문의 수 및 최신순 확인
        List<Question> questions = questionPage.getContent();
        assertThat(questions).hasSize(2);
        assertThat(questions.get(0).getQuestion()).isEqualTo("Question 2"); // 최신 질문
        assertThat(questions.get(1).getQuestion()).isEqualTo("Question 1"); // 이전 질문
    }

}