package com.dodream.book.service;

import com.dodream.book.domain.QuestionRequest;
import com.dodream.book.domain.QuestionResponse;
import com.dodream.user.entity.User;

public interface QuestionService {

    QuestionResponse addQuestion(Long id, User user, QuestionRequest questionRequest);

    void deleteQuestion(Long id, Long questionId, User user);
}
