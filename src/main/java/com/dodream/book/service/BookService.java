package com.dodream.book.service;

import com.dodream.book.domain.BookRequest;
import com.dodream.book.domain.BookResponse;
import com.dodream.book.domain.BookUpdateRequest;
import com.dodream.book.domain.BookUpdateResponse;
import com.dodream.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {

    Page<BookResponse> getBooks(String category, Pageable pageable, boolean sortByBookmarks);

    Page<BookResponse> getBookList(Pageable pageable, boolean sortByBookmarks);

    Page<BookResponse> getBookListByCategory(Pageable pageable, String category, boolean sortByBookmarks);

    Page<BookResponse> searchBooksByKeyword(String keyword, Pageable pageable);

    BookResponse addBook(User user, BookRequest bookRequest);

    BookUpdateResponse updateBook(User user, Long id, BookUpdateRequest request);

    void deleteBook(Long id, User user);

    Page<BookResponse> getPopularBooks();;
}

