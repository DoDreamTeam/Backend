package com.dodream.book.service;

import com.dodream.book.domain.QuestionRequest;
import com.dodream.book.domain.QuestionResponse;
import com.dodream.book.entity.Book;
import com.dodream.book.entity.Question;
import com.dodream.book.repository.BookRepository;
import com.dodream.book.repository.QuestionRepository;
import com.dodream.common.exception.BaseException;
import com.dodream.common.exception.ErrorCode;
import com.dodream.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;
    private final BookRepository bookRepository;

    // 문제 생성하기
    @Override
    @Transactional
    public QuestionResponse addQuestion(Long id, User user, QuestionRequest questionRequest) {
        // 문제집 확인
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new BaseException(ErrorCode.BOOK_NOT_FOUND));

        // 문제집 소유자 확인
        if (!book.getUser().getId().equals(user.getId())) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }

        // 문제 저장
        Question question = Question.builder()
            .question(questionRequest.getQuestion())
            .modelAnswer(questionRequest.getModelAnswer())
            .book(book)
            .build();
        Question savedQuestion = questionRepository.save(question);

        return QuestionResponse
            .builder()
            .id(savedQuestion.getId())
            .bookId(book.getId())
            .question(savedQuestion.getQuestion())
            .modelAnswer(savedQuestion.getModelAnswer())
            .createdAt(savedQuestion.getCreatedAt())
            .build();
    }

    // 문제 삭제
    @Override
    @Transactional
    public void deleteQuestion(Long id, Long questionId, User user) {
        // 문제집 확인
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new BaseException(ErrorCode.BOOK_NOT_FOUND));

        // 문제집 소유자 확인
        if (!book.getUser().getId().equals(user.getId())) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }

        // 문제 확인
        Question question = questionRepository.findById(questionId)
            .orElseThrow(() -> new BaseException(ErrorCode.QUESTION_NOT_FOUND));

        questionRepository.delete(question);
    }
}
