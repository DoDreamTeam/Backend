package com.dodream.book.service;

import com.dodream.book.domain.BookCommentRequest;
import com.dodream.book.domain.BookCommentResponse;
import com.dodream.user.entity.User;
import java.util.List;

public interface BookCommentService {

    List<BookCommentResponse> getCommentList(Long id);

    BookCommentResponse addComment(Long id, User user, BookCommentRequest bookCommentRequest);
}