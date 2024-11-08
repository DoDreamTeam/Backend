package com.dodream.mypage.service;

import com.dodream.book.domain.BookResponse;
import com.dodream.book.domain.BookUpdateResponse;
import com.dodream.book.entity.Book;
import com.dodream.book.entity.BookComment;
import com.dodream.book.entity.BookCommentLike;
import com.dodream.book.entity.Bookmark;
import com.dodream.book.entity.UserAnswer;
import com.dodream.book.enumtype.Evaluation;
import com.dodream.book.repository.BookCommentLikeRepository;
import com.dodream.book.repository.BookCommentRepository;
import com.dodream.book.repository.BookRepository;
import com.dodream.book.repository.BookmarkRepository;
import com.dodream.book.repository.UserAnswerRepository;
import com.dodream.common.exception.BaseException;
import com.dodream.common.exception.ErrorCode;
import com.dodream.mypage.domain.BookCommentLikeResponse;
import com.dodream.mypage.domain.BookCommentResponse;
import com.dodream.mypage.domain.GetUserAnswerResponse;
import com.dodream.mypage.domain.QueCommentLikeResponse;
import com.dodream.mypage.domain.QueCommentResponse;
import com.dodream.mypage.domain.UserInfoResponse;
import com.dodream.study.entity.QueComment;
import com.dodream.study.entity.QueCommentLike;
import com.dodream.study.repository.QueCommentLikeRepository;
import com.dodream.study.repository.QueCommentRepository;
import com.dodream.user.entity.User;
import com.dodream.user.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPageMeServiceImpl implements MyPageMeService {

    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;
    private final BookRepository bookRepository;
    private final BookCommentRepository bookCommentRepository;
    private final BookCommentLikeRepository bookCommentLikeRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final QueCommentRepository queCommentRepository;
    private final QueCommentLikeRepository queCommentLikeRepository;

    // 사용자 정보 가져오기 (userName , profileImage , userBooks )
    @Override
    @Transactional(readOnly = true)
    public UserInfoResponse getUserProfile() {
        User user = getAuthenticatedUser();
        User foundUser = userRepository.findById(user.getId())
            .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
        return UserInfoResponse.toProfileDTO(foundUser);
    }

    // 문제집 목록 가져오기
    @Override
    @Transactional(readOnly = true)
    public Page<BookResponse> getUserBooksAll(Pageable pageable) {
        return getBookResponse(
            bookRepository.findByUserIdOrderByCreatedAtDesc(getAuthenticatedUser().getId(), pageable),
            pageable
        );
    }

    // 북마크 한 문제집 목록 가져오기
    @Override
    @Transactional(readOnly = true)
    public Page<BookResponse> getUserBookmarks(Pageable pageable) {
        return getBookResponse(
            bookmarkRepository.findByUserIdAndIsDeletedFalseOrderByBookCreatedAtDesc(getAuthenticatedUser().getId(), pageable),
            pageable
        );
    }

    // 문제집 공개 비공개 설정
    @Override
    @Transactional
    public BookUpdateResponse updateSecret(Long bookId) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new BaseException(ErrorCode.BOOK_NOT_FOUND));

        User user = getAuthenticatedUser();
        bookOwner(book, user);

        book.setSecret(!book.isSecret());
        bookRepository.save(book);

        return BookUpdateResponse.builder()
            .id(book.getId())
            .secret(book.isSecret())
            .build();
    }

    private void bookOwner(Book book, User user) {
        if (!book.getUser().getId().equals(user.getId())) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }
    }

    // 사용자의 문제집 댓글 목록 조회
    @Override
    @Transactional(readOnly = true)
    public Page<BookCommentResponse> getUserComment(Pageable pageable) {
        User user = getAuthenticatedUser();

        Page<BookComment> comments = bookCommentRepository.findByUserIdOrderByCreatedAtDesc(
            user.getId(), pageable);

        if (comments.isEmpty()) {
            throw new BaseException(ErrorCode.BOOK_COMMENT_NOT_FOUND);
        }
        List<BookCommentResponse> responses = comments.stream()
            .map(comment -> BookCommentResponse.builder()
                .id(comment.getId())
                .bookId(comment.getBook().getId())
                .comment(comment.getComment())
                .userId(comment.getUser() != null ? comment.getUser().getId() : null)
                .username(comment.getUser() != null ? comment.getUser().getUsername() : null)
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build())
            .collect(Collectors.toList());
        return new PageImpl<>(responses, pageable, comments.getTotalElements());
    }

    // 사용자의 문제집 댓글 좋아요 목록
    @Override
    @Transactional(readOnly = true)
    public Page<BookCommentLikeResponse> getUserCommentLike(Pageable pageable) {
        User user = getAuthenticatedUser();
        Page<BookCommentLike> commentLikes = bookCommentLikeRepository
            .findByUserIdAndIsDeletedFalseOrderByCommentId_CreatedAtDesc(
                user.getId(), pageable);

        if (commentLikes.isEmpty()) {
            throw new BaseException(ErrorCode.COMMENT_LIKE_NOT_FOUND);
        }
        List<BookCommentLikeResponse> responses = commentLikes.stream()
            .map(commentLike -> {
                BookComment comment = commentLike.getCommentId();
                return BookCommentLikeResponse.builder()
                    .commentId(comment.getId())
                    .comment(comment.getComment())
                    .bookId(comment.getBook().getId())
                    .userId(comment.getUser() != null ? comment.getUser().getId() : null)
                    .userName(comment.getUser() != null ? comment.getUser().getUsername() : null)
                    .createdAt(comment.getCreatedAt())
                    .build();
            })
            .collect(Collectors.toList());
        return new PageImpl<>(responses, pageable, commentLikes.getTotalElements());
    }

    // 사용자가 푼 문제 목록 (전체)
    @Override
    @Transactional(readOnly = true)
    public Page<GetUserAnswerResponse> getUserAnswer(Pageable pageable) {
        User user = getAuthenticatedUser();
        Page<UserAnswer> userAnswers = userAnswerRepository.findByUserIdOrderByCreatedAtDesc(
            user.getId(), pageable);

        if (userAnswers.isEmpty()) {
            throw new BaseException(ErrorCode.USER_ANSWER_NOT_FOUND);
        }

        List<GetUserAnswerResponse> responses = userAnswers.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, userAnswers.getTotalElements());
    }

    // 사용자가 푼 문제 목록 (애매해요, 모르겠어요)
    @Override
    @Transactional(readOnly = true)
    public Page<GetUserAnswerResponse> getUserAnswerByEvaluation(String evaluation, Pageable pageable) {
        User user = getAuthenticatedUser();

        Evaluation evaluationEnum;
        try {
            evaluationEnum = Evaluation.valueOf(evaluation.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BaseException(ErrorCode.USER_ANSWER_EVALUATION_ERROR);
        }

        Page<UserAnswer> userAnswers = userAnswerRepository
            .findByUserIdAndEvaluationOrderByCreatedAtDesc(user.getId(), evaluationEnum, pageable);

        if (userAnswers.isEmpty()) {
            throw new BaseException(ErrorCode.USER_ANSWER_NOT_FOUND);
        }

        List<GetUserAnswerResponse> responses = userAnswers.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, userAnswers.getTotalElements());
    }

    // 스터디 댓글 조회
    @Override
    @Transactional(readOnly = true)
    public Page<QueCommentResponse> getStudyComment(Pageable pageable) {
        User user = getAuthenticatedUser();

        Page<QueComment> queComment = queCommentRepository.findByUserIdOrderByCreatedAtDesc(
            user.getId(), pageable);

        if (queComment.isEmpty()) {
            throw new BaseException(ErrorCode.BOOK_COMMENT_NOT_FOUND);
        }

        List<QueCommentResponse> responses = queComment.stream()
            .map(comment -> QueCommentResponse.builder()
                .id(comment.getId())
                .studyAnswerId(comment.getStudyAnswer().getId())
                .comment(comment.getContent())
                .studyId(comment.getStudyAnswer().getStudy().getId())
                .userId(comment.getUser() != null ? comment.getUser().getId() : null)
                .username(comment.getUser() != null ? comment.getUser().getUsername() : null)
                .studyTitle(comment.getStudyAnswer().getStudy().getTitle())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build())
            .collect(Collectors.toList());
        return new PageImpl<>(responses, pageable, queComment.getTotalElements());
    }

    // 스터디 댓글 좋아요 조회
    @Override
    @Transactional(readOnly = true)
    public Page<QueCommentLikeResponse> getStudyCommentLike(Pageable pageable) {
        User user = getAuthenticatedUser();
        Page<QueCommentLike> queCommentLikes = queCommentLikeRepository
            .findByUserIdAndIsDeletedFalseOrderByQuecomment_CreatedAtDesc(
                user.getId(), pageable);

        if (queCommentLikes.isEmpty()) {
            throw new BaseException(ErrorCode.COMMENT_LIKE_NOT_FOUND);
        }

        List<QueCommentLikeResponse> responses = queCommentLikes.stream()
            .map(queCommentLike -> {
                QueComment queComment = queCommentLike.getQuecomment();
                return QueCommentLikeResponse.builder()
                    .commentId(queComment.getId())
                    .comment(queComment.getContent())
                    .studyAnswerId(queComment.getStudyAnswer().getId())
                    .studyId(queComment.getStudyAnswer().getStudy().getId())
                    .userId(queComment.getUser() != null ? queComment.getUser().getId() : null)
                    .userName(queComment.getUser() != null ? queComment.getUser().getUsername() : null)
                    .createdAt(queComment.getCreatedAt())
                    .build();
            })
            .collect(Collectors.toList());
        return new PageImpl<>(responses, pageable, queCommentLikes.getTotalElements());
    }

    private Page<BookResponse> getBookResponse(Page<?> page, Pageable pageable) {
        // 모든 BookResponse 생성
        List<BookResponse> bookResponses = page.getContent().stream()
            .map(item -> {
                Book book;
                if (item instanceof Bookmark) {
                    book = ((Bookmark) item).getBook();
                } else {
                    book = (Book) item;
                }
                return convertToBookResponse(book, getAuthenticatedUser());
            })
            .collect(Collectors.toList());

        return new PageImpl<>(bookResponses, pageable, page.getTotalElements());
    }

    private BookResponse convertToBookResponse(Book book, User user) {
        boolean isBookmarked = (user != null) && bookmarkRepository.existsByUserIdAndBookIdAndIsDeletedFalse(user.getId(), book.getId());

        return BookResponse.builder()
            .id(book.getId())
            .title(book.getTitle())
            .userId(book.getUser().getId())
            .userProfile(book.getUser().getProfileImage())
            .bookmarkCount(bookmarkRepository.countByBookAndIsDeletedFalse(book))
            .username(book.getUser() != null ? book.getUser().getUsername() : null)
            .category(book.getCategory().name())
            .createdAt(book.getCreatedAt())
            .secret(book.isSecret())
            .isBookmarked(isBookmarked)
            .build();
    }

    private GetUserAnswerResponse mapToResponse(UserAnswer answer) {
        return GetUserAnswerResponse.builder()
            .id(answer.getId())
            .questionId(answer.getQuestion().getId())
            .bookId(answer.getQuestion().getBook().getId())
            .title(answer.getQuestion().getQuestion())
            .createdAt(answer.getCreatedAt())
            .evaluation(answer.getEvaluation().getEvaluation())
            .userId(answer.getUser().getId())
            .build();
    }

    private User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
