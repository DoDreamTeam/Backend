package com.dodream.book.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dodream.book.domain.AnswerDetailsResponse;
import com.dodream.book.domain.UserAnswerRequest;
import com.dodream.book.domain.UserAnswerResponse;
import com.dodream.book.entity.Question;
import com.dodream.book.entity.UserAnswer;
import com.dodream.book.enumtype.Evaluation;
import com.dodream.book.repository.QuestionRepository;
import com.dodream.book.repository.UserAnswerRepository;
import com.dodream.study.entity.Study;
import com.dodream.study.entity.StudyUserAnswer;
import com.dodream.study.repository.StudyRepository;
import com.dodream.study.repository.StudyUserAnswerRepository;
import com.dodream.user.entity.User;
import com.dodream.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class UserAnswerServiceTest {

    @InjectMocks
    private UserAnswerServiceImpl userAnswerService;

    @Mock
    private UserAnswerRepository userAnswerRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private StudyRepository studyRepository;

    @Mock
    private StudyUserAnswerRepository studyUserAnswerRepository;

    private User user;
    private Question question;

    @BeforeEach
    void setUp() {
        userAnswerRepository = mock(UserAnswerRepository.class);
        questionRepository = mock(QuestionRepository.class);
        studyRepository = mock(StudyRepository.class);
        studyUserAnswerRepository = mock(StudyUserAnswerRepository.class);

        userAnswerService = new UserAnswerServiceImpl(userAnswerRepository, questionRepository,
            studyRepository, studyUserAnswerRepository);

        user = User.builder().id(1L).build();
        question = Question.builder().id(1L).modelAnswer("Sample answer").question("Sample question").build();
    }

    @DisplayName("문제 풀기 성공")
    @Test
    void testSolveQuestion_Success() {
        UserAnswerRequest request = new UserAnswerRequest();
        request.setAnswer("My answer");

        when(questionRepository.findById(anyLong())).thenReturn(Optional.of(question));
        when(userAnswerRepository.findByUserIdAndQuestionId(anyLong(), anyLong())).thenReturn(null);
        when(userAnswerRepository.save(any(UserAnswer.class))).thenAnswer(invocation -> {
            UserAnswer answer = invocation.getArgument(0);
            return UserAnswer.builder()
                .id(1L)  // Set the ID here
                .user(answer.getUser())
                .question(answer.getQuestion())
                .answer(answer.getAnswer())
                .evaluation(answer.getEvaluation())
                .build();
        });

        UserAnswerResponse response = userAnswerService.solveQuestion(1L, 1L, user, request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("My answer", response.getAnswer());
        assertEquals("EVALUATION_BEFORE", response.getEvaluation());
    }

    @DisplayName("답안 평가 성공")
    @Test
    void testEvaluateAnswer_Success() {
        UserAnswer userAnswer = UserAnswer.builder()
            .id(1L)
            .user(user)
            .question(question)
            .answer("My answer")
            .evaluation(Evaluation.EVALUATION_BEFORE)
            .build();

        when(userAnswerRepository.findById(anyLong())).thenReturn(Optional.of(userAnswer));

        UserAnswerResponse response = userAnswerService.evaluateAnswer(1L, "EVALUATION_DONE");

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("My answer", response.getAnswer());
        assertEquals("EVALUATION_DONE", response.getEvaluation());
    }

    @DisplayName("답안 세부정보 조회 성공")
    @Test
    void testGetAnswerDetails_Success() {
        UserAnswer userAnswer = UserAnswer.builder()
            .id(1L)
            .user(user)
            .question(question)
            .answer("My answer")
            .build();

        when(questionRepository.findById(anyLong())).thenReturn(Optional.of(question));
        when(userAnswerRepository.findById(anyLong())).thenReturn(Optional.of(userAnswer));

        AnswerDetailsResponse response = userAnswerService.getAnswerDetails(1L, 1L, 1L, user);

        assertNotNull(response);
        assertEquals("Sample question", response.getQuestion());
        assertEquals("Sample answer", response.getModelAnswer());
        assertEquals("My answer", response.getUserAnswer());
    }

    @DisplayName("문제를 스터디에 추가하기 성공")
    @Test
    void testAddQuestionToMyStudies_Success() {
        UserAnswer userAnswer = UserAnswer.builder()
            .id(1L)
            .user(user)
            .question(question)
            .answer("My answer")
            .build();

        when(questionRepository.findById(anyLong())).thenReturn(Optional.of(question));
        when(userAnswerRepository.findByUserAndQuestion(any(User.class), any(Question.class)))
            .thenReturn(Optional.of(userAnswer));

        Study study = Study.builder().id(1L).build();
        when(studyRepository.findById(anyLong())).thenReturn(Optional.of(study));

        userAnswerService.addQuestionToMyStudies(user, 1L, List.of(1L));

        ArgumentCaptor<List<StudyUserAnswer>> captor = ArgumentCaptor.forClass(List.class);
        verify(studyUserAnswerRepository).saveAll(captor.capture());

        List<StudyUserAnswer> savedAnswers = captor.getValue();
        assertEquals(1, savedAnswers.size());
        assertEquals(1L, savedAnswers.get(0).getUserAnswer().getId());
        assertEquals(1L, savedAnswers.get(0).getStudy().getId());
    }

}
