package com.dodream.book.service;

import com.dodream.book.domain.AnswerDetailsResponse;
import com.dodream.book.domain.UserAnswerRequest;
import com.dodream.book.domain.UserAnswerResponse;
import com.dodream.book.entity.Question;
import com.dodream.book.entity.UserAnswer;
import com.dodream.book.enumtype.Evaluation;
import com.dodream.book.repository.QuestionRepository;
import com.dodream.book.repository.UserAnswerRepository;
import com.dodream.common.exception.BaseException;
import com.dodream.common.exception.ErrorCode;
import com.dodream.study.entity.Study;
import com.dodream.study.entity.StudyUserAnswer;
import com.dodream.study.repository.StudyRepository;
import com.dodream.study.repository.StudyUserAnswerRepository;
import com.dodream.user.entity.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserAnswerServiceImpl implements UserAnswerService{
    private final UserAnswerRepository userAnswerRepository;
    private final QuestionRepository questionRepository;
    private final StudyRepository studyRepository;
    private final StudyUserAnswerRepository studyUserAnswerRepository;

    // 문제 풀기
    @Override
    @Transactional(readOnly = true)
    public UserAnswerResponse solveQuestion(Long bookId, Long questionId, User user, UserAnswerRequest userAnswerRequest) {
        // 문제 확인
        Question question = questionRepository.findById(questionId)
            .orElseThrow(() -> new BaseException(ErrorCode.QUESTION_NOT_FOUND));

        // 기존 답안 확인
        UserAnswer existingAnswer = userAnswerRepository.findByUserIdAndQuestionId(user.getId(), questionId);

        if (existingAnswer != null) {
            // 기존 답안이 있을 경우 업데이트
            existingAnswer.updateAnswer(userAnswerRequest.getAnswer(), Evaluation.EVALUATION_BEFORE);
        } else {
            // 사용자 답안 저장
            UserAnswer userAnswer = UserAnswer.builder()
                .user(user)
                .question(question)
                .answer(userAnswerRequest.getAnswer())
                .evaluation(Evaluation.EVALUATION_BEFORE)  // 기본값 설정
                .build();

            existingAnswer = userAnswerRepository.save(userAnswer);
        }

        // UserAnswerResponse 반환
        return UserAnswerResponse.builder()
            .id(existingAnswer.getId())
            .userId(existingAnswer.getUser().getId() != null ? existingAnswer.getUser().getId() : user.getId())
            .questionId(existingAnswer.getQuestion().getId())
            .answer(existingAnswer.getAnswer())
            .evaluation(String.valueOf(existingAnswer.getEvaluation()))
            .build();
    }

    // 문제 평가하기
    @Override
    @Transactional(readOnly = true)
    public UserAnswerResponse evaluateAnswer(Long answerId, String evaluationRequest) {
        // 답안 확인
        UserAnswer userAnswer = userAnswerRepository.findById(answerId)
            .orElseThrow(() -> new BaseException(ErrorCode.QUESTION_NOT_FOUND));

        // 문자열을 Enum으로 변환
        Evaluation evaluation;
        try {
            evaluation = Evaluation.valueOf(evaluationRequest);
        } catch (IllegalArgumentException e) {
            throw new BaseException(ErrorCode.INVALID_EVALUATION);
        }

        // 평가하기 전인지 확인
        if (evaluation == Evaluation.EVALUATION_BEFORE) {
            throw new BaseException(ErrorCode.INVALID_EVALUATION);
        }

        // 평가 업데이트
        userAnswer.updateEvaluation(evaluation);

        return UserAnswerResponse.builder()
            .id(userAnswer.getId())
            .userId(userAnswer.getUser().getId())
            .questionId(userAnswer.getQuestion().getId())
            .answer(userAnswer.getAnswer())
            .evaluation(userAnswer.getEvaluation().getEvaluation())
            .build();
    }

    // 문제 제출 이후 답변 페이지 (질문+모범답안+내답안)
    @Override
    @Transactional(readOnly = true)
    public AnswerDetailsResponse getAnswerDetails(Long bookId, Long questionId, Long answerId, User user) {
        // 문제 확인
        Question question = questionRepository.findById(questionId)
            .orElseThrow(() -> new BaseException(ErrorCode.QUESTION_NOT_FOUND));

        // 사용자 답안 확인
        UserAnswer userAnswer = userAnswerRepository.findById(answerId)
            .orElseThrow(() -> new BaseException(ErrorCode.ANSWER_NOT_FOUND));

        // 사용자가 작성한 답안인지 확인
        if (!userAnswer.getUser().getId().equals(user.getId())) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }

        // 모범답안
        String modelAnswer = question.getModelAnswer();

        // AnswerDetailsResponse 생성
        return AnswerDetailsResponse.builder()
            .question(question.getQuestion()) // 질문 내용
            .modelAnswer(modelAnswer) // 모범답안
            .userAnswer(userAnswer.getAnswer()) // 사용자가 작성한 답안
            .build();
    }

    // 문제 제출 이후 내가 참여하는 스터디에 추가하기
    @Override
    @Transactional
    public void addQuestionToMyStudies(User user, Long questionId, List<Long> studyIds) {
        // 문제 확인
        Question question = questionRepository.findById(questionId)
            .orElseThrow(() -> new BaseException(ErrorCode.QUESTION_NOT_FOUND));

        // 사용자 답변을 가져옴
        UserAnswer userAnswer = userAnswerRepository.findByUserAndQuestion(user, question)
            .orElseThrow(() -> new BaseException(ErrorCode.USER_ANSWER_NOT_FOUND));

        List<StudyUserAnswer> studyUserAnswers = new ArrayList<>();

        for (Long studyId : studyIds) {
            Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new BaseException(ErrorCode.STUDY_NOT_FOUND));

            // StudyUserAnswer 객체 생성
            StudyUserAnswer studyUserAnswer = StudyUserAnswer.builder()
                .userAnswer(userAnswer) // UserAnswer 객체 설정
                .study(study)
                .createdAt(LocalDateTime.now())
                .build();

            studyUserAnswers.add(studyUserAnswer);
        }

        // 모든 StudyUserAnswer 저장
        studyUserAnswerRepository.saveAll(studyUserAnswers);
    }

}
