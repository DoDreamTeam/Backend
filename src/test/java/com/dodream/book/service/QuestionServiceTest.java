package com.dodream.book.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
import com.dodream.book.repository.UserAnswerRepository;
import com.dodream.book.repository.UserBookRepository;
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
    private UserBookRepository userBookRepository; // Added
    @Mock
    private UserAnswerRepository userAnswerRepository; // Added

    private User user;
    private Book book;
    private QuestionRequest questionRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder().id(1L).username("testuser").provider("provider1").providerId("1").build();

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
        Page<QuestionListResponse> result = questionService.getQuestions(PageRequest.of(0, 2), book.getId(), user, null);

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

        // When
        QuestionResponse response = questionService.updateQuestion(1L, 1L, updateRequest, user);

        // Then
        assertNotNull(response);
        assertEquals("Updated question", response.getQuestion());
        assertEquals("Old answer", response.getModelAnswer());
        assertEquals(1L, response.getBookId());
        assertNotNull(response.getCreatedAt());

        // Verify that the question was updated correctly
        assertEquals("Updated question", question.getQuestion());
        assertEquals("Old answer", question.getModelAnswer());
    }

    @DisplayName("문제 수정 시 문제집이 잘못된 경우 수정 실패")
    @Test
    void testUpdateQuestion_BookNotFound() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        BaseException exception = assertThrows(BaseException.class, () ->
            questionService.updateQuestion(1L, 1L, questionRequest, user));

        // Then
        assertEquals(ErrorCode.BOOK_NOT_FOUND, exception.getErrorCode());
    }

    @DisplayName("문제 수정 시 다른 사용자가 수정 시도할 경우 수정 실패")
    @Test
    void testUpdateQuestion_AccessDenied() {
        // Given
        User anotherUser = User.builder().id(2L).build(); // Another user
        Question question = Question.builder()
            .id(1L)
            .question("Existing question")
            .modelAnswer("Existing answer")
            .book(book) // Make sure this question belongs to the same book
            .createdAt(LocalDateTime.now())
            .build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question)); // Mock the question retrieval

        // When
        BaseException exception = assertThrows(BaseException.class, () ->
            questionService.updateQuestion(1L, 1L, questionRequest, anotherUser));

        // Then
        assertEquals(ErrorCode.ACCESS_DENIED, exception.getErrorCode());
    }


    @DisplayName("문제 제목으로 검색 성공")
    @Test
    void testSearchQuestionsByTitle_Success() {
        // Given
        Question question = Question.builder()
            .id(1L)
            .question("Searchable Question")
            .modelAnswer("Old answer")
            .book(book)
            .createdAt(LocalDateTime.now())
            .build();

        List<Question> questionList = List.of(question);
        Page<Question> questionPage = new PageImpl<>(questionList);

        when(questionRepository.findByBookIdAndQuestionContaining(eq(book.getId()), anyString(), any(Pageable.class)))
            .thenReturn(questionPage);

        // When
        Page<QuestionListResponse> result = questionService.searchQuestions(book.getId(), "Searchable", Pageable.ofSize(10));

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Searchable Question", result.getContent().get(0).getQuestion());
    }

}
