package com.dodream.study.service;

import com.dodream.common.exception.BaseException;
import com.dodream.common.exception.ErrorCode;
import com.dodream.study.domain.NoticeCommentRequest;
import com.dodream.study.domain.NoticeCommentResponse;
import com.dodream.study.domain.NoticeCommentUpdateRequest;
import com.dodream.study.domain.NoticeCommentUpdateResponse;
import com.dodream.study.entity.Notice;
import com.dodream.study.entity.NoticeComment;
import com.dodream.study.repository.NoticeCommentLikeRepository;
import com.dodream.study.repository.NoticeCommentRepository;
import com.dodream.study.repository.NoticeRepository;
import com.dodream.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoticeCommentServiceImpl implements NoticeCommentService {

    private final NoticeCommentRepository noticeCommentRepository;
    private final NoticeCommentLikeRepository noticeCommentLikeRepository;
    private final NoticeRepository noticeRepository;

    @Override
    public NoticeCommentResponse addNoticeComment(Long id, User user,
        NoticeCommentRequest noticeCommentRequest) {

        // 공지사항 정보 조회
        Notice notice = noticeRepository.findById(id)
            .orElseThrow(() -> new BaseException(ErrorCode.NOTICE_NOT_FOUND));

        NoticeComment noticeComment = NoticeComment.builder()
            .content(noticeCommentRequest.getContent())
            .user(user)
            .notice(notice)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        NoticeComment savedComment = noticeCommentRepository.save(noticeComment);

        return NoticeCommentResponse.builder()
            .id(savedComment.getId())
            .content(savedComment.getContent())
            .username(user.getUsername())
            .noticeId(savedComment.getNotice().getId())
            .likeCount(0L)
            .createdAt(savedComment.getCreatedAt())
            .updatedAt(savedComment.getUpdatedAt())
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "getNotice")
    public Page<NoticeCommentResponse> getNoticeCommentList(Pageable pageable, Long id,
        boolean isSortByLikes) {

        // 공지사항 ID가 존재하지 않는 경우 예외 처리
        if (!noticeRepository.existsById(id)) {
            throw new BaseException(ErrorCode.NOTICE_NOT_FOUND);
        }

        Page<NoticeComment> contents;
        // 좋아요 순으로 정렬
        contents = isSortByLikes ?
            noticeCommentRepository.findByNoticeIdOrderByLikeCountDesc(pageable, id):
            noticeCommentRepository.findByNoticeIdOrderByCreatedAtDesc(pageable, id);

        List<NoticeCommentResponse> responses = contents.stream()
            .map(notiComment -> NoticeCommentResponse.builder()
                .id(notiComment.getId())
                .content(notiComment.getContent())
                .username(notiComment.getUser().getUsername() != null ? notiComment.getUser().getUsername() : null)
                .likeCount(noticeCommentLikeRepository.countByNoticeCommentIdAndIsDeletedFalse(notiComment))
                .noticeId(notiComment.getNotice().getId())
                .createdAt(notiComment.getCreatedAt())
                .build())
            .toList();

        return new PageImpl<>(responses, pageable, contents.getTotalElements());
    }

    @Override
    public void deleteNoticeComment(Long commentId, User user) {
        NoticeComment noticeComment = noticeCommentRepository.findById(commentId)
            .orElseThrow(() -> new BaseException(ErrorCode.NOTICE_COMMENT_NOT_FOUND));

        if (!noticeComment.getUser().getId().equals(user.getId())) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }

        noticeCommentRepository.delete(noticeComment);
    }

    @Override
    @Transactional
    public NoticeCommentUpdateResponse updateNoticeComment(Long commentId, User user,
        NoticeCommentUpdateRequest noticeCommentUpdateRequest) {
        // 공지사항 댓글 정보 조회
        NoticeComment noticeComment = noticeCommentRepository.findById(commentId)
            .orElseThrow(() -> new BaseException(ErrorCode.NOTICE_COMMENT_NOT_FOUND));

        // 공지사항 댓글 작성자 확인
        if (!noticeComment.getUser().getId().equals(user.getId())) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }

        // 공지사항 댓글 수정
        noticeComment.updateContent(noticeCommentUpdateRequest.getContent());

        return NoticeCommentUpdateResponse.builder()
            .content(noticeComment.getContent())
            .build();
    }
}
