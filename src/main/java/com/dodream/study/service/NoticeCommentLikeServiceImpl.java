package com.dodream.study.service;

import com.dodream.common.exception.BaseException;
import com.dodream.common.exception.ErrorCode;
import com.dodream.study.domain.NoticeCommentLikeResponse;
import com.dodream.study.entity.NoticeComment;
import com.dodream.study.entity.NoticeCommentLike;
import com.dodream.study.repository.NoticeCommentLikeRepository;
import com.dodream.study.repository.NoticeCommentRepository;
import com.dodream.user.entity.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoticeCommentLikeServiceImpl implements NoticeCommentLikeService {

    private final NoticeCommentLikeRepository noticeCommentLikeRepository;
    private final NoticeCommentRepository noticeCommentRepository;

    @Override
    @Transactional
    public NoticeCommentLikeResponse toggleNoticeCommentLike(User user, Long commentId) {
        // 좋아요할 댓글 확인
        NoticeComment noticeComment = noticeCommentRepository.findById(commentId)
            .orElseThrow(() -> new BaseException(ErrorCode.NOTICE_COMMENT_NOT_FOUND));

        // 좋아요 조회
        Optional<NoticeCommentLike> existingLike
            = noticeCommentLikeRepository.findByUserAndNoticeCommentId(user, noticeComment);
        NoticeCommentLikeResponse likeResponse;

        // 이미 좋아요가 존재하는 경우 (좋아요 취소)
        if (existingLike.isPresent()) {
            NoticeCommentLike like = existingLike.get();
            boolean newIsDeleted = !like.isDeleted();       // 새로운 isDeleted 값 계산

            // 업데이트된 좋아요 저장
            noticeCommentLikeRepository.save(NoticeCommentLike.builder()
                .id(like.getId())
                .isDeleted(newIsDeleted)
                .user(like.getUser())
                .noticeCommentId(like.getNoticeCommentId())
                .build());

            likeResponse = NoticeCommentLikeResponse.builder()
                .id(like.getId())
                .isDeleted(newIsDeleted)
                .userId(user.getId())
                .noticeCommentId(noticeComment.getId())
                .build();

        } else {
            // 새로운 좋아요 생성
            NoticeCommentLike newLike = NoticeCommentLike.builder()
                .user(user)
                .noticeCommentId(noticeComment)
                .isDeleted(false)
                .build();

            // 새로운 좋아요 저장
            noticeCommentLikeRepository.save(newLike);

            likeResponse = NoticeCommentLikeResponse.builder()
                .id(newLike.getId())
                .isDeleted(false)
                .userId(user.getId())
                .noticeCommentId(noticeComment.getId())
                .build();
        }

        return likeResponse;
    }

}
