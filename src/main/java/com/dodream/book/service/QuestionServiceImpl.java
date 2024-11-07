package com.dodream.book.service;

import com.dodream.book.domain.AddToMyBooksRequest;
import com.dodream.book.domain.AddToMyBooksResponse;
import com.dodream.book.domain.EvaluationResponse;
import com.dodream.book.domain.QuestionRequest;
import com.dodream.book.domain.QuestionResponse;
import com.dodream.book.domain.QuestionListResponse;
import com.dodream.book.entity.Book;
import com.dodream.book.entity.Question;
import com.dodream.book.entity.UserAnswer;
import com.dodream.book.entity.UserBook;
import com.dodream.book.repository.BookRepository;
import com.dodream.book.repository.QuestionRepository;
import com.dodream.book.repository.UserAnswerRepository;
import com.dodream.book.repository.UserBookRepository;
import com.dodream.common.exception.BaseException;
import com.dodream.common.exception.ErrorCode;
import com.dodream.study.repository.StudyUserAnswerRepository;
import com.dodream.user.entity.User;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    private final UserBookRepository userBookRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final StudyUserAnswerRepository studyUserAnswerRepository;

    // 문제 전체 조회
    @Override
    @Transactional(readOnly = true)
    public Page<QuestionListResponse> getQuestions(Pageable pageable, Long bookId, User user) {
        // 최신순으로 문제 조회
        Page<Question> questions = questionRepository.findByBookIdOrderByCreatedAtDesc(pageable, bookId);

        // 사용자가 푼 문제 ID 목록 수집
        Set<Long> answeredQuestionIds = new HashSet<>();

        // 로그인한 사용자가 평가한 문제에 대한 정보를 포함
        List<QuestionListResponse> questionResponses = questions.getContent().stream()
            .map(question -> {
                EvaluationResponse evaluationResponse = null;

                if (user != null) {
                    // 사용자가 푼 문제에 대한 평가 정보를 가져옵니다.
                    Optional<UserAnswer> userAnswer = userAnswerRepository.findByUserAndQuestion(user, question);
                    if (userAnswer.isPresent()) {
                        // 사용자가 해당 문제에 대해 평가한 정보가 있으면 포함
                        evaluationResponse = new EvaluationResponse(
                            userAnswer.get().getEvaluation().getEvaluation(), // 평가 내용
                            userAnswer.get().getUser().getId(), // 평가한 사용자 ID
                            userAnswer.get().getCreatedAt() // 평가한 날짜
                        );
                    }
                }

                // QuestionListResponse 객체 생성하여 반환
                return QuestionListResponse.builder()
                    .id(question.getId())
                    .question(question.getQuestion())
                    .createdAt(question.getCreatedAt())
                    .evaluation(evaluationResponse) // 평가 정보 포함
                    .build();
            })
            .collect(Collectors.toList());

        // Page 객체 반환 (페이징 처리)
        return new PageImpl<>(questionResponses, pageable, questions.getTotalElements());
    }

    // 내가 푼 문제 제외하고 조회
    @Override
    @Transactional(readOnly = true)
    public Page<QuestionListResponse> getQuestionsExceptMy(Pageable pageable, Long bookId, User user) {
        // 1. 사용자가 푼 문제 목록을 가져옵니다.
        List<Long> answeredQuestionIds = userAnswerRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
            .map(userAnswer -> userAnswer.getQuestion().getId())
            .toList();

        // 2. 문제 조회 (최신순으로 전체 조회 - Pageable 없이)
        List<Question> allQuestions = questionRepository.findByBookIdOrderByCreatedAtDesc(bookId);

        // 3. 내가 푼 문제를 제외한 목록 생성
        List<Question> filteredQuestions = allQuestions.stream()
            .filter(question -> !answeredQuestionIds.contains(question.getId()))
            .collect(Collectors.toList());

        // 4. 페이지 처리 (필터링된 문제에 대해서만 페이징 처리)
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredQuestions.size());
        List<Question> pagedQuestions = filteredQuestions.subList(start, end);

        // 5. 필터링된 문제에서 해당 페이지에 맞는 문제를 가져옴 (Page 객체 반환을 위해)
        List<QuestionListResponse> questionResponses = pagedQuestions.stream()
            .map(question -> {
                EvaluationResponse evaluationResponse = null;

                // 로그인한 유저의 평가 정보가 있으면 포함
                Optional<UserAnswer> userAnswer = userAnswerRepository.findByUserAndQuestion(user, question);
                if (userAnswer.isPresent()) {
                    evaluationResponse = new EvaluationResponse(
                        userAnswer.get().getEvaluation().getEvaluation(),
                        userAnswer.get().getUser().getId(),
                        userAnswer.get().getCreatedAt()
                    );
                }

                return QuestionListResponse.builder()
                    .id(question.getId())
                    .question(question.getQuestion())
                    .createdAt(question.getCreatedAt())
                    .evaluation(evaluationResponse)
                    .build();
            })
            .collect(Collectors.toList());

        // 필터링된 문제들의 총 개수와 페이징 정보를 이용해서 PageImpl 반환
        return new PageImpl<>(questionResponses, pageable, filteredQuestions.size());
    }

    // 문제 개별 조회
    @Override
    @Transactional(readOnly = true)
    public QuestionResponse getOneQuestion(Long id, Long questionId) {
        // 문제집 확인
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new BaseException(ErrorCode.BOOK_NOT_FOUND));

        // 문제 확인
        Question question = questionRepository.findById(questionId)
            .orElseThrow(() -> new BaseException(ErrorCode.QUESTION_NOT_FOUND));

        return QuestionResponse
            .builder()
            .id(question.getId())
            .question(question.getQuestion())
            .modelAnswer(question.getModelAnswer())
            .createdAt(question.getCreatedAt())
            .build();
    }

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

    // 문제 수정
    @Override
    @Transactional
    public QuestionResponse updateQuestion(Long bookId, Long questionId, QuestionRequest updateRequest, User user) {
        // 문제집 확인
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new BaseException(ErrorCode.BOOK_NOT_FOUND));

        // 문제 확인
        Question question = questionRepository.findById(questionId)
            .orElseThrow(() -> new BaseException(ErrorCode.QUESTION_NOT_FOUND));

        // 문제집 소유자 확인
        if (!book.getUser().getId().equals(user.getId())) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }

        // 수정할 내용 적용
        String newQuestion = updateRequest.getQuestion();
        String newModelAnswer = updateRequest.getModelAnswer();

        question.updateQuestion(
            newQuestion != null ? newQuestion : question.getQuestion(),
            newModelAnswer != null ? newModelAnswer : question.getModelAnswer()
        );

        return QuestionResponse.builder()
            .id(question.getId())
            .bookId(book.getId())
            .question(question.getQuestion())
            .modelAnswer(question.getModelAnswer())
            .createdAt(question.getCreatedAt())
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

        studyUserAnswerRepository.deleteByUserAnswerId(questionId);
        userAnswerRepository.deleteByQuestionId(questionId);
        userBookRepository.deleteByQuestionId(question);
        questionRepository.delete(question);
    }

    // 문제를 내 문제집에 추가
    @Override
    @Transactional
    public AddToMyBooksResponse addQuestionToBooks(Long id, Long questionId,
        AddToMyBooksRequest request, User user) {
        List<Long> targetBooks = request.getBookIds();
        List<Long> addedBooks = new ArrayList<>();

        for (Long bookId : targetBooks) {
            boolean added = addQuestionToBook(bookId, questionId, user);
            if (added) {
                addedBooks.add(bookId);
            }
        }

        return AddToMyBooksResponse.builder()
            .questionId(questionId)
            .addedToBooks(addedBooks)
            .userId(user.getId())
            .build();
    }

    private boolean addQuestionToBook(Long bookId, Long questionId, User user) {
        try {

            // 문제집 확인
            Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BaseException(ErrorCode.BOOK_NOT_FOUND));

            // 문제 확인
            Question originalQuestion = questionRepository.findById(questionId)
                .orElseThrow(() -> new BaseException(ErrorCode.QUESTION_NOT_FOUND));

            // 새로운 문제 생성
            Question newQuestion = Question.builder()
                .question(originalQuestion.getQuestion())
                .modelAnswer(originalQuestion.getModelAnswer())
                .book(book) // 새로 생성되는 문제는 해당 문제집에 연결
                .build();

            // 새로운 문제 저장
            Question savedQuestion = questionRepository.save(newQuestion);

            // UserBook 엔티티 생성
            UserBook userBook = UserBook.builder()
                .user(user) // 현재 사용자
                .book(book)
                .questionId(savedQuestion) // 새로 생성된 문제 설정
                .build();

            // UserBook 저장
            userBookRepository.save(userBook);

            return true; // 성공적으로 추가되면 true 반환
        } catch (Exception e) {
            return false; // 실패하면 false 반환
        }
    }

    // 문제 제목으로 검색하기
    @Override
    @Transactional(readOnly = true)
    public Page<QuestionListResponse> searchQuestions(Long bookId, String keyword, Pageable pageable) {
        Page<Question> questions = questionRepository.findByBookIdAndQuestionContainingOrderByCreatedAtDesc(bookId, keyword, pageable);

        return questions.map(question -> QuestionListResponse.builder()
            .id(question.getId())
            .question(question.getQuestion())
            .createdAt(question.getCreatedAt())
            .build());
    }

}
