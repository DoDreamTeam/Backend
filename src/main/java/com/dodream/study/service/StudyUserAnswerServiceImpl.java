package com.dodream.study.service;

import com.dodream.book.entity.Question;
import com.dodream.book.entity.UserAnswer;
import com.dodream.book.repository.UserAnswerRepository;
import com.dodream.common.exception.BaseException;
import com.dodream.common.exception.ErrorCode;
import com.dodream.study.domain.StudyUserAnswerResponse;
import com.dodream.study.entity.StudyUserAnswer;
import com.dodream.study.repository.StudyUserAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudyUserAnswerServiceImpl implements StudyUserAnswerService {

    private final StudyUserAnswerRepository studyUserAnswerRepository;
    private final UserAnswerRepository userAnswerRepository;

    @Override
    public StudyUserAnswerResponse getStudyUserAnswer(Long id) {
        StudyUserAnswer studyUserAnswer = studyUserAnswerRepository.findById(id)
            .orElseThrow(() -> new BaseException(
                ErrorCode.STUDY_USER_ANSWER_NOT_FOUND));

        UserAnswer userAnswer = studyUserAnswer.getUserAnswer();
        Question question = userAnswer.getQuestion();

        return StudyUserAnswerResponse.builder()
            .id(studyUserAnswer.getId())
            .question(question.getQuestion())
            .modelAnswer(question.getModelAnswer())
            .answer(userAnswer.getAnswer())
            .userId(userAnswer.getUser().getId())
            .profileImage(userAnswer.getUser().getProfileImage())
            .userName(userAnswer.getUser().getUsername())
            .createdAt(studyUserAnswer.getCreatedAt())
            .build();
    }
}
