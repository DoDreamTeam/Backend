package com.dodream.book.service;

import com.dodream.book.domain.BookCommentResponse;
import java.util.List;

public interface BookCommentService {

    List<BookCommentResponse> getCommentList(Long id);
}
