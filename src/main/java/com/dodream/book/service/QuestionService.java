package com.dodream.book.service;

import com.dodream.book.domain.QuestionAddRequest;
import com.dodream.book.domain.QuestionAddResponse;
import com.dodream.book.domain.QuestionListResponse;
import com.dodream.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuestionService {

    QuestionAddResponse addQuestion(Long id, User user, QuestionAddRequest questionRequest);

    void deleteQuestion(Long id, Long questionId, User user);

    Page<QuestionListResponse> getQuestions(Pageable pageable, Long id);
}
