package com.dodream.book.service;

import com.dodream.book.domain.AnswerDetailsResponse;
import com.dodream.book.domain.UserAnswerRequest;
import com.dodream.book.domain.UserAnswerResponse;
import com.dodream.user.entity.User;
import java.util.List;

public interface UserAnswerService {

    UserAnswerResponse solveQuestion(Long bookId, Long questionId, User user, UserAnswerRequest userAnswerRequest);

    UserAnswerResponse evaluateAnswer(Long answerId, String evaluationRequest);

    AnswerDetailsResponse getAnswerDetails(Long bookId, Long questionId, Long answerId, User user);

    void addQuestionToMyStudies(User user, Long questionId, List<Long> studyIds);

}
