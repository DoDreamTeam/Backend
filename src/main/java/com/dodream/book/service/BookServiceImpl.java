package com.dodream.book.service;

import com.dodream.book.domain.BookRequest;
import com.dodream.book.domain.BookResponse;
import com.dodream.book.entity.Book;
import com.dodream.book.repository.BookRepository;
import com.dodream.book.repository.BookmarkRepository;
import com.dodream.common.enumtype.Category;
import com.dodream.common.exception.BaseException;
import com.dodream.common.exception.ErrorCode;
import com.dodream.user.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookmarkRepository bookmarkRepository;

    @Override
    public List<BookResponse> getBooks(String category) {
        if (category != null) {
            return getBookListByCategory(category);
        } else {
            return getBookList();
        }
    }

    @Override
    public List<BookResponse> getBookList() {
        List<Book> bookList = bookRepository.findAllBySecretFalse();

        return convertToBookResponseList(bookList);
    }

    @Override
    public List<BookResponse> getBookListByCategory(String category) {
        Category categoryEnum;
        try {
            categoryEnum = Category.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            // 잘못된 카테고리인 경우 예외처리
            throw new BaseException(ErrorCode.BOOK_CATEGORY_ERROR);
        }

        List<Book> bookList = bookRepository.findAllByCategoryAndSecretFalse(categoryEnum);

        // 해당 카테고리에 문제집이 없는 경우 예외처리
        if (bookList.isEmpty()) {
            throw new BaseException(ErrorCode.BOOK_CATEGORY_NOT_FOUND);
        }
        return convertToBookResponseList(bookList);
    }

    @Override
    public List<BookResponse> searchBooksByKeyword(String keyword) {
        List<Book> bookList = bookRepository.findAllByTitleContainingAndSecretFalse(keyword);

        // 해당 검색어와 일치하는 문제집이 없는 경우 예외처리
        if(bookList.isEmpty()) {
            throw new BaseException(ErrorCode.BOOK_SEARCH_NOT_FOUND);
        }

        return convertToBookResponseList(bookList);
    }

    @Override
    public BookResponse addBook(User user, BookRequest bookRequest) {
        Book book = bookRequest.toEntity(user);
        Book savedBook = bookRepository.save(book);

        return BookResponse.builder()
            .id(savedBook.getId())
            .title(savedBook.getTitle())
            .username(user.getUsername())  // 현재 로그인한 사용자 이름
            .bookmarkCount(0L)             // 초기 북마크 수
            .category(savedBook.getCategory().name())
            .createdAt(savedBook.getCreatedAt()) // 실제 생성된 날짜 사용
            .build();
    }

    private List<BookResponse> convertToBookResponseList(List<Book> bookList) {
        return bookList.stream()
            .map(book -> BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .username(book.getUser() != null ? book.getUser().getUsername() : null)
                .bookmarkCount(bookmarkRepository.countByBookAndIsDeletedFalse(book)) // 북마크 수 카운트
                .category(book.getCategory().name())
                .createdAt(book.getCreatedAt())
                .build())
            .toList();
    }
}
