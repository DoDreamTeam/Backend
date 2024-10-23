package com.dodream.book.service;

import com.dodream.book.domain.BookCommentResponse;
import com.dodream.book.entity.BookComment;
import com.dodream.book.repository.BookCommentLikeRepository;
import com.dodream.book.repository.BookCommentRepository;
import com.dodream.common.exception.BaseException;
import com.dodream.common.exception.ErrorCode;
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
}