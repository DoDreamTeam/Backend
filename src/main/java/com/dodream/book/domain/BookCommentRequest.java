package com.dodream.book.domain;

import com.dodream.book.entity.BookComment;
import com.dodream.user.entity.User;
import lombok.Data;

@Data
public class BookCommentRequest {
    private String comment;

    public BookComment toEntity(User user) {
        return BookComment
            .builder()
            .comment(comment)
            .user(user)
            .build();
    }
}