package com.dodream.book.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.dodream.book.entity.Book;
import com.dodream.book.entity.Question;
import com.dodream.book.entity.UserAnswer;
import com.dodream.book.enumtype.Evaluation;
import com.dodream.common.enumtype.Category;
import com.dodream.user.entity.User;
import com.dodream.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class UserAnswerRepositoryTest {
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAnswerRepository userAnswerRepository;

    private Book book;
    private Question question;
    private User user; // 사용자 객체를 클래스 수준에서 선언

    @BeforeEach
    void setUp() {
        user = User.builder().username("hello").provider("provider1").providerId("1").build();
        user = userRepository.save(user); // 사용자 저장 후 객체를 다시 할당

        book = Book
            .builder()
            .title("add test book")
            .user(user)
            .category(Category.CATEGORY_CERT)
            .secret(false)
            .build();
        bookRepository.save(book);

        question = Question
            .builder()
            .question("test question?")
            .modelAnswer("answer")
            .build();
        questionRepository.save(question);
    }

    @DisplayName("사용자가 특정 질문에 대한 답변을 저장하고 조회할 수 있다.")
    @Test
    public void testFindByUserAndQuestion() {
        // given (사전 준비)
        UserAnswer userAnswer = UserAnswer.builder()
            .user(user)
            .question(question)
            .answer("test answer")
            .evaluation(Evaluation.EVALUATION_BEFORE)
            .build();
        userAnswerRepository.save(userAnswer); // 답변 저장

        // when (테스트 진행할 범위)
        Optional<UserAnswer> foundAnswer = userAnswerRepository.findByUserAndQuestion(user, question);

        // then (범위에 대한 결과 검증)
        assertTrue(foundAnswer.isPresent());
        assertEquals("test answer", foundAnswer.get().getAnswer());
    }

    @DisplayName("사용자가 특정 질문을 푼 기록을 조회할 수 있다.")
    @Test
    public void testFindByUserIdAndQuestionId() {
        // given (사전 준비)
        UserAnswer userAnswer = UserAnswer.builder()
            .user(user)
            .question(question)
            .answer("test answer")
            .evaluation(Evaluation.EVALUATION_SOSO)
            .build();
        userAnswerRepository.save(userAnswer); // 답변 저장

        // when (테스트 진행할 범위)
        UserAnswer foundAnswer = userAnswerRepository.findByUserIdAndQuestionId(user.getId(), question.getId());

        // then (범위에 대한 결과 검증)
        assertNotNull(foundAnswer);
        assertEquals(userAnswer.getAnswer(), foundAnswer.getAnswer());
    }

    @DisplayName("사용자가 푼 문제 목록을 페이지로 조회할 수 있다.")
    @Test
    public void testFindByUserIdOrderByCreatedAtDesc() {
        // given (사전 준비)
        UserAnswer userAnswer1 = UserAnswer.builder()
            .user(user)
            .question(question)
            .answer("first answer")
            .evaluation(Evaluation.EVALUATION_SOSO)
            .createdAt(LocalDateTime.now().minusDays(1)) // 1일 전
            .build();
        userAnswerRepository.save(userAnswer1); // 첫 번째 답변 저장

        UserAnswer userAnswer2 = UserAnswer.builder()
            .user(user)
            .question(question)
            .answer("second answer")
            .evaluation(Evaluation.EVALUATION_SOSO)
            .createdAt(LocalDateTime.now()) // 현재 시간
            .build();
        userAnswerRepository.save(userAnswer2); // 두 번째 답변 저장

        // when (테스트 진행할 범위)
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserAnswer> answersPage = userAnswerRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable);

        // then (범위에 대한 결과 검증)
        assertFalse(answersPage.isEmpty());
        assertEquals(2, answersPage.getTotalElements());
        assertEquals("first answer", answersPage.getContent().get(0).getAnswer()); // 최근 답변이 첫 번째
    }
}