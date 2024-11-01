package com.dodream.study.service;

import com.dodream.common.exception.BaseException;
import com.dodream.common.exception.ErrorCode;
import com.dodream.study.domain.QueCommentRequest;
import com.dodream.study.domain.QueCommentResponse;
import com.dodream.study.domain.QueCommentUpdateRequest;
import com.dodream.study.domain.QueCommentUpdateResponse;
import com.dodream.study.entity.QueComment;
import com.dodream.study.entity.StudyUserAnswer;
import com.dodream.study.repository.QueCommentLikeRepository;
import com.dodream.study.repository.QueCommentRepository;
import com.dodream.study.repository.StudyUserAnswerRepository;
import com.dodream.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QueCommentServiceImpl implements QueCommentService {

    private final QueCommentRepository queCommentRepository;
    private final QueCommentLikeRepository queCommentLikeRepository;
    private final StudyUserAnswerRepository studyUserAnswerRepository;

    @Override
    public QueCommentResponse addQueComment(Long id, User user, QueCommentRequest queCommentRequest) {

        StudyUserAnswer studyAnswer = studyUserAnswerRepository.findById(id)
                .orElseThrow(() -> new BaseException(ErrorCode.STUDY_USER_ANSWER_NOT_FOUND));
//
        QueComment queComment = QueComment.builder()
                .content(queCommentRequest.getContent())
                .user(user)
                .studyAnswer(studyAnswer)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        QueComment savedComment = queCommentRepository.save(queComment);

        return QueCommentResponse.builder()
                .id(savedComment.getId())
                .content(savedComment.getContent())
                .username(user.getUsername())
                .userAnswerId(savedComment.getStudyAnswer().getId())
                .likeCount(0L)
                .createdAt(savedComment.getCreatedAt())
                .updatedAt(savedComment.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "getUserAnswer")
    public Page<QueCommentResponse> getQueCommentList(Pageable pageable, Long id,
        User user, boolean isSortByLikes) {

        // 공지사항 ID 가 존재하지 않는 경우 예외 처리
        if (!studyUserAnswerRepository.existsById(id)) {
            throw new BaseException(ErrorCode.STUDY_USER_ANSWER_NOT_FOUND);
        }
        Page<QueComment> contents;
        // 좋아요 순으로 정렬
        contents = isSortByLikes ?
                queCommentRepository.findByStudyAnswerIdOrderByLikeCountDesc(pageable, id) :
                queCommentRepository.findByStudyAnswerIdOrderByCreatedAtDesc(pageable, id);

        List<QueCommentResponse> responses = contents.stream()
            .map(queComment -> {
                boolean isLiked = (user != null)
                    && queCommentLikeRepository.existsByUserIdAndQuecommentIdAndIsDeletedFalse(
                    user.getId(), queComment);

                return QueCommentResponse.builder()
                    .id(queComment.getId())
                    .content(queComment.getContent())
                    .username(queComment.getUser().getUsername() != null ? queComment.getUser().getUsername() : null)
                    .likeCount(queCommentLikeRepository.countByQueCommentIdAndIsDeletedFalse(queComment))
                    .userAnswerId(queComment.getStudyAnswer().getId())
                    .profileImage(queComment.getUser().getProfileImage())
                    .createdAt(queComment.getCreatedAt())
                    .isLiked(isLiked)
                    .build();
            })
            .toList();
        return new PageImpl<>(responses, pageable, contents.getTotalElements());
    }

    @Override
    public void deleteQueComment(Long commentId, User user) {
        QueComment queComment = queCommentRepository.findById(commentId)
                .orElseThrow(() -> new BaseException(ErrorCode.QUE_COMMENT_NOT_FOUND));

        if (!queComment.getUser().getId().equals(user.getId())) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }
        queCommentRepository.delete(queComment);
    }

    @Override
    public QueCommentUpdateResponse updateQueComment(Long commentId, User user, QueCommentUpdateRequest queCommentUpdateRequest) {
        // 스터디 문제 댓글 정보 조회
        QueComment queComment = queCommentRepository.findById(commentId)
                .orElseThrow(() -> new BaseException(ErrorCode.QUE_COMMENT_NOT_FOUND));
        
        // 스터디 문제 댓글 작성자 확인
        if (!queComment.getUser().getId().equals(user.getId())) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }
        // 공지사항 댓글 수정
        queComment.updateContent(queCommentUpdateRequest.getContent());

        return QueCommentUpdateResponse.builder()
                .content(queComment.getContent())
                .build();
    }
}

