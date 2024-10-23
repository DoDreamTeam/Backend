package com.dodream.book.service;

import com.dodream.book.domain.BookCommentRequest;
import com.dodream.book.domain.BookCommentResponse;
import com.dodream.book.domain.BookCommentUpdateRequest;
import com.dodream.book.domain.BookCommentUpdateResponse;
import com.dodream.book.entity.Book;
import com.dodream.book.entity.BookComment;
import com.dodream.book.repository.BookCommentLikeRepository;
import com.dodream.book.repository.BookCommentRepository;
import com.dodream.book.repository.BookRepository;
import com.dodream.common.exception.BaseException;
import com.dodream.common.exception.ErrorCode;
import com.dodream.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookCommentServiceImpl implements BookCommentService {

    private final BookCommentRepository bookCommentRepository;
    private final BookCommentLikeRepository bookCommentLikeRepository;
    private final BookRepository bookRepository;


    @Override
    @Transactional(readOnly = true)
    public List<BookCommentResponse> getCommentList(Long id) {
        List<BookComment> comments = bookCommentRepository.findByBookIdOrderByCreatedAtDesc(id);

        // 문제집 id가 존재하지 않는 경우 예외 처리
        if (comments.isEmpty()) {
            throw new BaseException(ErrorCode.BOOK_ID_NOT_FOUND);
        }

        return comments.stream()
            .map(comment -> {
                return BookCommentResponse.builder()
                    .id(comment.getId())
                    .comment(comment.getComment())
                    .username(comment.getUser().getUsername() != null ? comment.getUser().getUsername() : null)
                    .likeCount(bookCommentLikeRepository.countByCommentId(comment)) // 좋아요 수 카운트
                    .bookId(comment.getBook().getId())
                    .createdAt(comment.getCreatedAt())
                    .build();
            })
            .collect(Collectors.toList());
    }

    // 문제집 댓글 생성
    @Override
    @Transactional
    public BookCommentResponse addComment(Long id, User user,
        BookCommentRequest bookCommentRequest) {
        // 책 정보 조회
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new BaseException(ErrorCode.BOOK_NOT_FOUND));

        BookComment comment = BookComment.builder()
            .comment(bookCommentRequest.getComment())
            .user(user)
            .book(book)
            .createdAt(LocalDateTime.now())
            .build();

        // 댓글 저장
        BookComment savedComment = bookCommentRepository.save(comment);

        // 응답 객체 생성
        return BookCommentResponse.builder()
            .id(savedComment.getId())
            .comment(savedComment.getComment())
            .username(user.getUsername())
            .bookId(savedComment.getBook().getId())
            .likeCount(0L) // 초기 좋아요 수 0
            .createdAt(savedComment.getCreatedAt())
            .build();
    }

    // 문제집 댓글 수정
    @Override
    @Transactional
    public BookCommentUpdateResponse updateComment(Long commentId, User user, BookCommentUpdateRequest bookCommentUpdateRequest) {
        // 댓글 정보 조회
        BookComment comment = bookCommentRepository.findById(commentId)
            .orElseThrow(() -> new BaseException(ErrorCode.BOOK_COMMENT_NOT_FOUND));

        // 댓글 작성자 확인
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }

        // 댓글 수정
        comment.updateComment(bookCommentUpdateRequest.getComment()); // 댓글 내용과 updatedAt 갱신

        // 응답 객체 생성
        return BookCommentUpdateResponse.builder()
            .comment(comment.getComment()) // 수정된 댓글 내용 반환
            .build();
    }
}