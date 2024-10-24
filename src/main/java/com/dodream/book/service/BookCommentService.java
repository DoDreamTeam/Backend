package com.dodream.book.service;

import com.dodream.book.domain.BookCommentRequest;
import com.dodream.book.domain.BookCommentResponse;
import com.dodream.book.domain.BookCommentUpdateRequest;
import com.dodream.book.domain.BookCommentUpdateResponse;
import com.dodream.user.entity.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookCommentService {

    Page<BookCommentResponse> getCommentList(Pageable pageable, Long id, boolean isSortByLikes);

    BookCommentResponse addComment(Long id, User user, BookCommentRequest bookCommentRequest);

    BookCommentUpdateResponse updateComment(Long commentId, User user, BookCommentUpdateRequest bookCommentUpdateRequest);

    void deleteComment(Long commentId, User user);
}