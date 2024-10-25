package com.dodream.study.service;

import com.dodream.study.domain.NoticeCommentRequest;
import com.dodream.study.domain.NoticeCommentResponse;
import com.dodream.study.domain.NoticeCommentUpdateRequest;
import com.dodream.study.domain.NoticeCommentUpdateResponse;
import com.dodream.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoticeCommentService {

    NoticeCommentResponse addNoticeComment(Long id, User user, NoticeCommentRequest noticeCommentRequest);

    Page<NoticeCommentResponse> getNoticeCommentList(Pageable pageable, Long id, boolean isSortByLikes);

    void deleteNoticeComment(Long commentId, User user);

    NoticeCommentUpdateResponse updateNoticeComment(Long commentId, User user, NoticeCommentUpdateRequest noticeCommentUpdateRequest);

}
