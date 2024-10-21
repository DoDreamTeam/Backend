package com.dodream.book.domain;

import com.dodream.book.entity.Book;
import com.dodream.common.enumtype.Category;
import com.dodream.user.entity.User;
import lombok.Data;

@Data
public class BookRequest {
    private String title;
    private String username;
    private Category category;
    private boolean secret;

    public Book toEntity(User user) {
        return Book
            .builder()
            .title(title)
            .user(user)
            .category(category)
            .secret(secret)
            .build();
    }
}