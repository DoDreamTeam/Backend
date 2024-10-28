package com.dodream.study.service;

import com.dodream.common.exception.BaseException;
import com.dodream.common.exception.ErrorCode;
import com.dodream.study.domain.QueCommentLikeResponse;
import com.dodream.study.entity.QueComment;
import com.dodream.study.entity.QueCommentLike;
import com.dodream.study.repository.QueCommentLikeRepository;
import com.dodream.study.repository.QueCommentRepository;
import com.dodream.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QueCommentLikeServiceImpl implements QueCommentLikeService {

    private final QueCommentLikeRepository queCommentLikeRepository;
    private final QueCommentRepository queCommentRepository;

    @Override
    @Transactional
    public QueCommentLikeResponse toggleQueCommentLike(User user, Long commentId) {

        // 좋아요할 댓글 확인
        QueComment queComment = queCommentRepository.findById(commentId)
                .orElseThrow(() -> new BaseException(ErrorCode.QUE_COMMENT_NOT_FOUND));

        // 좋아요 조회
        Optional<QueCommentLike> existingLike
                = queCommentLikeRepository.findByUserAndQuecomment(user, queComment);
        QueCommentLikeResponse likeResponse;

        // 이미 좋아요가 존재하는 경우 (좋아요 취소)
        if (existingLike.isPresent()) {
            QueCommentLike like = existingLike.get();
            boolean newIsDeleted = !like.isDeleted();

            // 업데이트된 좋아요 저장
            queCommentLikeRepository.save(QueCommentLike.builder()
                    .id(like.getId())
                    .isDeleted(newIsDeleted)
                    .user(like.getUser())
                    .quecomment(like.getQuecomment())
                    .build());

            likeResponse = QueCommentLikeResponse.builder()
                    .id(like.getId())
                    .isDeleted(newIsDeleted)
                    .userId(like.getUser().getId())
                    .queComment(queComment.getId())
                    .build();
        } else {
            // 새로운 좋아요 생성
            QueCommentLike newLike = QueCommentLike.builder()
                    .user(user)
                    .quecomment(queComment)
                    .isDeleted(false)
                    .build();

            // 새로운 좋아요 저장ㅇ
            queCommentLikeRepository.save(newLike);

            likeResponse = QueCommentLikeResponse.builder()
                    .id(newLike.getId())
                    .isDeleted(false)
                    .userId(user.getId())
                    .queComment(queComment.getId())
                    .build();
        }
        return likeResponse;
    }
}
