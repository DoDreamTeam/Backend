package com.dodream.book.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dodream.book.domain.QuestionRequest;
import com.dodream.book.domain.QuestionResponse;
import com.dodream.book.domain.QuestionListResponse;
import com.dodream.book.entity.Book;
import com.dodream.book.entity.Question;
import com.dodream.book.repository.BookRepository;
import com.dodream.book.repository.QuestionRepository;
import com.dodream.common.exception.BaseException;
import com.dodream.common.exception.ErrorCode;
import com.dodream.user.entity.User;
import com.dodream.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class QuestionServiceTest {

    @InjectMocks
    private QuestionServiceImpl questionService;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    private User user;
    private Book book;
    private QuestionRequest questionRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L); // 사용자 ID 설정

        book = new Book();
        book.setId(1L);
        book.setUser(user); // 문제집 소유자 설정

        questionRequest = new QuestionRequest();
        questionRequest.setQuestion("Sample question");
        questionRequest.setModelAnswer("Sample answer");
    }

    @DisplayName("문제 생성 성공")
    @Test
    void testAddQuestion_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(questionRepository.save(any(Question.class))).thenAnswer(invocation -> {
            Question question = invocation.getArgument(0);
            return Question.builder()
                .id(1L) // 저장된 문제 ID 설정
                .question(question.getQuestion())
                .modelAnswer(question.getModelAnswer())
                .book(book)
                .createdAt(LocalDateTime.now()) // 생성 날짜 설정
                .build();
        });

        QuestionResponse response = questionService.addQuestion(1L, user, questionRequest);

        assertNotNull(response);
        assertEquals("Sample question", response.getQuestion());
        assertEquals("Sample answer", response.getModelAnswer());
        assertEquals(1L, response.getBookId());
        assertNotNull(response.getCreatedAt());

        ArgumentCaptor<Question> captor = ArgumentCaptor.forClass(Question.class);
        verify(questionRepository).save(captor.capture());
        assertEquals("Sample question", captor.getValue().getQuestion());
        assertEquals(book, captor.getValue().getBook());
    }

    @DisplayName("문제집이 잘못된 경우 생성 실패")
    @Test
    void testAddQuestion_BookNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        BaseException exception = assertThrows(BaseException.class, () ->
            questionService.addQuestion(1L, user, questionRequest));
        assertEquals(ErrorCode.BOOK_NOT_FOUND, exception.getErrorCode());
    }

    @DisplayName("다른 사용자가 생성 시도 시 생성 실패")
    @Test
    void testAddQuestion_AccessDenied() {
        User anotherUser = User.builder()
            .id(2L) // 다른 사용자 설정
            .build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BaseException exception = assertThrows(BaseException.class, () ->
            questionService.addQuestion(1L, anotherUser, questionRequest));
        assertEquals(ErrorCode.ACCESS_DENIED, exception.getErrorCode());
    }

    @DisplayName("문제 삭제 성공")
    @Test
    void testDeleteQuestion_Success() {
        Question question = Question.builder()
            .id(1L)
            .book(book)
            .build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        questionService.deleteQuestion(1L, 1L, user);

        verify(questionRepository).delete(question);
    }

    @DisplayName("문제집 잘못된 경우 문제 삭제 실패")
    @Test
    void testDeleteQuestion_BookNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        BaseException exception = assertThrows(BaseException.class, () ->
            questionService.deleteQuestion(1L, 1L, user));
        assertEquals(ErrorCode.BOOK_NOT_FOUND, exception.getErrorCode());
    }

    @DisplayName("다른 사용자가 삭제 시도 시 문제 삭제 실패")
    @Test
    void testDeleteQuestion_AccessDenied() {
        User anotherUser = User.builder()
            .id(2L) // 다른 사용자 설정
            .build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BaseException exception = assertThrows(BaseException.class, () ->
            questionService.deleteQuestion(1L, book.getId(), anotherUser));
        assertEquals(ErrorCode.ACCESS_DENIED, exception.getErrorCode());
    }

    @DisplayName("문제가 잘못된 경우 문제 삭제 실패")
    @Test
    void testDeleteQuestion_QuestionNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(questionRepository.findById(1L)).thenReturn(Optional.empty());

        BaseException exception = assertThrows(BaseException.class, () ->
            questionService.deleteQuestion(1L, 1L, user));
        assertEquals(ErrorCode.QUESTION_NOT_FOUND, exception.getErrorCode());
    }

    @DisplayName("문제 전체 조회 성공")
    @Test
    void testGetQuestions_Success() {
        // Given
        Question question1 = Question.builder()
            .id(1L)
            .question("Question 1")
            .modelAnswer("Answer 1")
            .book(book)
            .createdAt(LocalDateTime.now().minusDays(1))
            .build();

        Question question2 = Question.builder()
            .id(2L)
            .question("Question 2")
            .modelAnswer("Answer 2")
            .book(book)
            .createdAt(LocalDateTime.now())
            .build();

        List<Question> questionList = List.of(question1, question2);
        Page<Question> questionPage = new PageImpl<>(questionList, PageRequest.of(0, 2), questionList.size());

        when(questionRepository.findByBookIdOrderByCreatedAtDesc(any(Pageable.class), eq(book.getId())))
            .thenReturn(questionPage);

        // When
        Page<QuestionListResponse> result = questionService.getQuestions(PageRequest.of(0, 2), book.getId());

        // Then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());

        List<QuestionListResponse> questionResponses = result.getContent();
        assertEquals(2, questionResponses.size());
        assertEquals("Question 1", questionResponses.get(0).getQuestion());
        assertEquals("Question 2", questionResponses.get(1).getQuestion());
    }

    @DisplayName("문제 수정 성공")
    @Test
    void testUpdateQuestion_Success() {
        // Given
        Question question = Question.builder()
            .id(1L)
            .question("Old question")
            .modelAnswer("Old answer")
            .book(book)
            .createdAt(LocalDateTime.now())
            .build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        QuestionRequest updateRequest = new QuestionRequest();
        updateRequest.setQuestion("Updated question");
        updateRequest.setModelAnswer("Updated answer");

        // When
        QuestionResponse response = questionService.updateQuestion(1L, 1L, updateRequest, user);

        // Then
        assertNotNull(response);
        assertEquals("Updated question", response.getQuestion());
        assertEquals("Updated answer", response.getModelAnswer());
        assertEquals(1L, response.getBookId());
        assertNotNull(response.getCreatedAt());

        // Verify that the question was updated
        assertEquals("Updated question", question.getQuestion());
        assertEquals("Updated answer", question.getModelAnswer());
    }

    @DisplayName("문제 수정 시 질문만 업데이트")
    @Test
    void testUpdateQuestion_OnlyQuestionUpdated() {
        // Given
        Question question = Question.builder()
            .id(1L)
            .question("Old question")
            .modelAnswer("Old answer")
            .book(book)
            .createdAt(LocalDateTime.now())
            .build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        QuestionRequest updateRequest = new QuestionRequest();
        updateRequest.setQuestion("Updated question");
        updateRequest.setModelAnswer(null); // 모범답안은 수정하지 않음

        // When
        QuestionResponse response = questionService.updateQuestion(1L, 1L, updateRequest, user);

        // Then
        assertNotNull(response);
        assertEquals("Updated question", response.getQuestion());
        assertEquals("Old answer", response.getModelAnswer()); // 모범답안은 변경되지 않음
    }

    @DisplayName("문제 수정 시 모범답안만 업데이트")
    @Test
    void testUpdateQuestion_OnlyModelAnswerUpdated() {
        // Given
        Question question = Question.builder()
            .id(1L)
            .question("Old question")
            .modelAnswer("Old answer")
            .book(book)
            .createdAt(LocalDateTime.now())
            .build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        QuestionRequest updateRequest = new QuestionRequest();
        updateRequest.setQuestion(null); // 질문은 수정하지 않음
        updateRequest.setModelAnswer("Updated answer");

        // When
        QuestionResponse response = questionService.updateQuestion(1L, 1L, updateRequest, user);

        // Then
        assertNotNull(response);
        assertEquals("Old question", response.getQuestion()); // 질문은 변경되지 않음
        assertEquals("Updated answer", response.getModelAnswer());
    }

    @DisplayName("문제가 잘못된 경우 문제 수정 실패")
    @Test
    void testUpdateQuestion_QuestionNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(questionRepository.findById(1L)).thenReturn(Optional.empty());

        QuestionRequest updateRequest = new QuestionRequest();
        updateRequest.setQuestion("Some question");
        updateRequest.setModelAnswer("Some answer");

        BaseException exception = assertThrows(BaseException.class, () ->
            questionService.updateQuestion(1L, 1L, updateRequest, user));
        assertEquals(ErrorCode.QUESTION_NOT_FOUND, exception.getErrorCode());
    }

    @DisplayName("문제집 잘못된 경우 문제 수정 실패")
    @Test
    void testUpdateQuestion_BookNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        QuestionRequest updateRequest = new QuestionRequest();
        updateRequest.setQuestion("Some question");
        updateRequest.setModelAnswer("Some answer");

        BaseException exception = assertThrows(BaseException.class, () ->
            questionService.updateQuestion(1L, 1L, updateRequest, user));
        assertEquals(ErrorCode.BOOK_NOT_FOUND, exception.getErrorCode());
    }

    @DisplayName("다른 사용자가 수정 시도 시 문제 수정 실패")
    @Test
    void testUpdateQuestion_AccessDenied() {
        User anotherUser = User.builder()
            .id(2L) // 다른 사용자 설정
            .build();

        // 문제집이 존재함
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        // 문제도 존재함
        Question question = Question.builder()
            .id(1L)
            .question("Existing question")
            .modelAnswer("Existing answer")
            .book(book)
            .build();
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        // 수정 요청
        QuestionRequest updateRequest = new QuestionRequest();
        updateRequest.setQuestion("Some question");
        updateRequest.setModelAnswer("Some answer");

        // AccessDenied 예외가 발생해야 함
        BaseException exception = assertThrows(BaseException.class, () ->
            questionService.updateQuestion(1L, 1L, updateRequest, anotherUser));
        assertEquals(ErrorCode.ACCESS_DENIED, exception.getErrorCode());
    }
}