package com.dodream.book.service;

import com.dodream.book.domain.QuestionAddRequest;
import com.dodream.book.domain.QuestionAddResponse;
import com.dodream.book.domain.QuestionListResponse;
import com.dodream.book.entity.Book;
import com.dodream.book.entity.Question;
import com.dodream.book.repository.BookRepository;
import com.dodream.book.repository.QuestionRepository;
import com.dodream.common.exception.BaseException;
import com.dodream.common.exception.ErrorCode;
import com.dodream.user.entity.User;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;
    private final BookRepository bookRepository;

    // 문제 전체 조회
    @Override
    @Transactional(readOnly = true)
    public Page<QuestionListResponse> getQuestions(Pageable pageable, Long id) {
        Page<Question> questions = questionRepository.findByBookIdOrderByCreatedAtDesc(pageable, id);

        // 비회원/회원이 하나도 풀지 않은 경우
        List<QuestionListResponse> questionResponses = questions.getContent().stream()
            .map(question -> QuestionListResponse.builder()
                .id(question.getId())
                .question(question.getQuestion())
                .createdAt(question.getCreatedAt())
                .build())
            .collect(Collectors.toList());

        // 회원이 한 문제라도 푼 경우 (문제 평가 기능 구현 이후 구현할 예정)

        return new PageImpl<>(questionResponses, pageable, questions.getTotalElements());
    }

    // 문제 생성하기
    @Override
    @Transactional
    public QuestionAddResponse addQuestion(Long id, User user, QuestionAddRequest questionRequest) {
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

        return QuestionAddResponse
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
