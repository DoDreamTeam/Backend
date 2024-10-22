package com.dodream.book.service;

import com.dodream.book.domain.BookRequest;
import com.dodream.book.domain.BookResponse;
import com.dodream.book.domain.BookUpdateRequest;
import com.dodream.book.domain.BookUpdateResponse;
import com.dodream.user.entity.User;
import java.util.List;

public interface BookService {

    List<BookResponse> getBooks(String category);

    List<BookResponse> getBookList();

    List<BookResponse> getBookListByCategory(String category);

    List<BookResponse> searchBooksByKeyword(String keyword);

    BookResponse addBook(User user, BookRequest bookRequest);

    BookUpdateResponse updateBook(User user, Long id, BookUpdateRequest request);

    void deleteBook(Long id, User user);

}

