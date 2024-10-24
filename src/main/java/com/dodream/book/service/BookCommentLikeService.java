package com.dodream.book.service;

import com.dodream.book.domain.BookCommentLikeResponse;
import com.dodream.user.entity.User;

public interface BookCommentLikeService {

    BookCommentLikeResponse toggleCommentLike(User user, Long commentId);

}
