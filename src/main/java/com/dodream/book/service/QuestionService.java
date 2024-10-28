package com.dodream.book.service;

import com.dodream.book.domain.AddToMyBooksRequest;
import com.dodream.book.domain.AddToMyBooksResponse;
import com.dodream.book.domain.QuestionRequest;
import com.dodream.book.domain.QuestionResponse;
import com.dodream.book.domain.QuestionListResponse;
import com.dodream.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuestionService {

    QuestionResponse addQuestion(Long id, User user, QuestionRequest questionRequest);

    void deleteQuestion(Long id, Long questionId, User user);

    Page<QuestionListResponse> getQuestions(Pageable pageable, Long id, User user, Boolean type);

    QuestionListResponse getOneQuestion(Long id, Long questionId);

    QuestionResponse updateQuestion(Long id, Long questionId, QuestionRequest questionRequest, User user);

    AddToMyBooksResponse addQuestionToBooks(Long id, Long questionId, AddToMyBooksRequest request, User user);

    Page<QuestionListResponse> searchQuestions(Long bookId, String keyword, Pageable pageable);
}