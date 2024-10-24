package com.dodream.book.service;

import com.dodream.book.domain.BookCommentLikeResponse;
import com.dodream.book.entity.BookComment;
import com.dodream.book.entity.BookCommentLike;
import com.dodream.book.repository.BookCommentLikeRepository;
import com.dodream.book.repository.BookCommentRepository;
import com.dodream.common.exception.BaseException;
import com.dodream.common.exception.ErrorCode;
import com.dodream.user.entity.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookCommentLikeServiceImpl implements BookCommentLikeService {
    private final BookCommentLikeRepository bookCommentLikeRepository;
    private final BookCommentRepository bookCommentRepository;

    @Override
    @Transactional
    public BookCommentLikeResponse toggleCommentLike(User user, Long commentId) {
        // 좋아요할 댓글 확인
        BookComment comment = bookCommentRepository.findById(commentId)
            .orElseThrow(() -> new BaseException(ErrorCode.BOOK_COMMENT_NOT_FOUND));

        // 좋아요 조회
        Optional<BookCommentLike> existingLike = bookCommentLikeRepository.findByUserAndCommentId(user, comment);
        BookCommentLikeResponse likeResponse;

        // 이미 좋아요가 존재하는 경우 (좋아요 취소)
        if (existingLike.isPresent()) {
            BookCommentLike like = existingLike.get();
            boolean newIsDeleted = !like.isDeleted(); // 새로운 isDeleted 값 계산

            // 업데이트된 좋아요 저장
            bookCommentLikeRepository.save(BookCommentLike.builder()
                .id(like.getId())
                .isDeleted(newIsDeleted)
                .user(like.getUser())
                .commentId(like.getCommentId())
                .build());

            // 응답 객체 생성
            likeResponse = BookCommentLikeResponse.builder()
                .id(like.getId())
                .isDeleted(newIsDeleted)
                .userId(user.getId())
                .commentId(comment.getId())
                .build();
        } else {
            // 새로운 좋아요 생성
            BookCommentLike newLike = BookCommentLike.builder()
                .user(user)
                .commentId(comment)
                .isDeleted(false) // 기본값은 false
                .build();

            // 새로운 좋아요 저장
            bookCommentLikeRepository.save(newLike);

            // 응답 객체 생성
            likeResponse = BookCommentLikeResponse.builder()
                .id(newLike.getId())
                .isDeleted(false)
                .userId(user.getId())
                .commentId(comment.getId())
                .build();
        }

        return likeResponse;

    }
}