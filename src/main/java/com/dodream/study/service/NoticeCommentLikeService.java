package com.dodream.study.service;

import com.dodream.study.domain.NoticeCommentLikeResponse;
import com.dodream.user.entity.User;

public interface NoticeCommentLikeService {
    NoticeCommentLikeResponse toggleNoticeCommentLike(User user, Long commentId);

}
