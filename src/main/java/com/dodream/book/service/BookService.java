package com.dodream.book.service;

import com.dodream.book.domain.BookResponse;
import java.util.List;

public interface BookService {

    List<BookResponse> getBookList();

    List<BookResponse> getBookListByCategory(String category);
}
