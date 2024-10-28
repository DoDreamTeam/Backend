package com.dodream.study.service;

import com.dodream.study.domain.QueCommentLikeResponse;
import com.dodream.user.entity.User;

public interface QueCommentLikeService {
    QueCommentLikeResponse toggleQueCommentLike(User user, Long commentId);
}
