package com.dodream.book.service;

import com.dodream.book.domain.BookmarkResponse;
import com.dodream.user.entity.User;

public interface BookmarkService {
    BookmarkResponse toggleBookmark(User user, Long id);
}