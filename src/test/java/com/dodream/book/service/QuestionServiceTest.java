package com.dodream.book.service;

import com.dodream.book.domain.AddToMyBooksRequest;
import com.dodream.book.domain.AddToMyBooksResponse;
import com.dodream.book.domain.QuestionListResponse;
import com.dodream.book.domain.QuestionRequest;
import com.dodream.book.domain.QuestionResponse;
import com.dodream.book.entity.Book;
import com.dodream.book.entity.Question;
import com.dodream.book.repository.BookRepository;
import com.dodream.book.repository.QuestionRepository;
import com.dodream.book.repository.UserAnswerRepository;
import com.dodream.book.repository.UserBookRepository;
import com.dodream.study.repository.StudyUserAnswerRepository;
import com.dodream.user.entity.User;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class QuestionServiceImplTest {

    @InjectMocks
    private QuestionServiceImpl questionService;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserBookRepository userBookRepository;

    @Mock
    private UserAnswerRepository userAnswerRepository;

    @Mock
    private StudyUserAnswerRepository studyUserAnswerRepository;

    private User user;
    private Book book;
    private Question question;
    private QuestionRequest questionRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize user and book objects
        user = User.builder().id(1L).username("testuser").provider("provider1").providerId("1").build();

        book = new Book();
        book.setId(1L);
        book.setUser(user); // Set the book owner

        question = Question.builder()
            .id(1L)
            .question("Sample question")
            .createdAt(LocalDateTime.now())
            .book(book)
            .build();
    }

    @DisplayName("문제 생성 성공")
    @Test
    void testAddQuestion_Success() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        QuestionRequest questionRequest = new QuestionRequest();
        questionRequest.setQuestion("Sample question");
        questionRequest.setModelAnswer("Sample answer");

        when(questionRepository.save(any(Question.class))).thenAnswer(invocation -> {
            Question question = invocation.getArgument(0);
            return Question.builder()
                .id(1L) // Set saved question ID
                .question(question.getQuestion())
                .modelAnswer(question.getModelAnswer())
                .book(book)
                .createdAt(LocalDateTime.now()) // Set creation date
                .build();
        });

        // When
        QuestionResponse response = questionService.addQuestion(1L, user, questionRequest);

        // Then
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

    @DisplayName("문제 수정 성공")
    @Test
    void testUpdateQuestion_Success() {
        // Given
        QuestionRequest updateRequest = new QuestionRequest();
        updateRequest.setQuestion("Updated question");
        updateRequest.setModelAnswer("Updated answer");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        // When
        QuestionResponse response = questionService.updateQuestion(1L, 1L, updateRequest, user);

        // Then
        assertNotNull(response);
        assertEquals("Updated question", response.getQuestion());
        assertEquals("Updated answer", response.getModelAnswer());
        assertEquals(1L, response.getBookId());
        assertNotNull(response.getCreatedAt());
    }

    @DisplayName("문제 삭제 성공")
    @Test
    void testDeleteQuestion_Success() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        // When
        questionService.deleteQuestion(1L, 1L, user);

        // Then
        verify(questionRepository, times(1)).delete(question);
        verify(userAnswerRepository, times(1)).deleteByQuestionId(1L);
        verify(studyUserAnswerRepository, times(1)).deleteByUserAnswerId(1L);
        verify(userBookRepository, times(1)).deleteByQuestionId(question);
    }

    @DisplayName("문제 검색 성공")
    @Test
    void testSearchQuestions_Success() {
        // Given
        String keyword = "Sample";

        Question question = Question.builder()
            .id(1L)
            .question("Sample question")
            .createdAt(LocalDateTime.now())
            .build();

        List<Question> questionList = List.of(question);
        Page<Question> questionPage = new PageImpl<>(questionList);

        when(questionRepository.findByBookIdAndQuestionContainingOrderByCreatedAtDesc(1L, keyword, Pageable.unpaged()))
            .thenReturn(questionPage);

        // When
        Page<QuestionListResponse> response = questionService.searchQuestions(1L, keyword, Pageable.unpaged());

        // Then
        assertNotNull(response);
        assertFalse(response.isEmpty());
        assertEquals(1, response.getTotalElements());

        QuestionListResponse questionResponse = response.getContent().get(0);
        assertEquals("Sample question", questionResponse.getQuestion());
        assertNotNull(questionResponse.getCreatedAt());
    }


    @DisplayName("문제집에 문제 추가 성공")
    @Test
    void testAddQuestionToBooks_Success() {
        // Given
        AddToMyBooksRequest request = new AddToMyBooksRequest();
        request.setBookIds(List.of(1L));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        // When
        AddToMyBooksResponse response = questionService.addQuestionToBooks(1L, 1L, request, user);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getQuestionId());
        assertTrue(response.getAddedToBooks().contains(1L));  // Assert the question is added to the book.
    }
}
