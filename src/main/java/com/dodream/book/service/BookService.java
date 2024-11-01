package com.dodream.book.service;

import com.dodream.book.domain.BookRequest;
import com.dodream.book.domain.BookResponse;
import com.dodream.book.domain.BookUpdateRequest;
import com.dodream.book.domain.BookUpdateResponse;
import com.dodream.user.entity.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {

    Page<BookResponse> getBooks(String category, Pageable pageable, boolean sortByBookmarks, User user);

    Page<BookResponse> getBookList(Pageable pageable, boolean sortByBookmarks, User user);

    Page<BookResponse> getBookListByCategory(Pageable pageable, String category, boolean sortByBookmarks, User user);

    Page<BookResponse> searchBooksByKeyword(String keyword, Pageable pageable, User user);

    BookResponse addBook(User user, BookRequest bookRequest);

    BookUpdateResponse updateBook(User user, Long id, BookUpdateRequest request);

    void deleteBook(Long id, User user);

    Page<BookResponse> getPopularBooks(User user);

    BookResponse getBook(Long id, User user);
}

