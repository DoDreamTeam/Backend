package com.dodream.book.service;

import com.dodream.book.domain.BookResponse;
import com.dodream.book.entity.Book;
import com.dodream.book.repository.BookRepository;
import com.dodream.book.repository.BookmarkRepository;
import com.dodream.common.enumtype.Category;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookmarkRepository bookmarkRepository;

    @Override
    public List<BookResponse> getBookList() {
        List<Book> bookList = bookRepository.findAllBySecretFalse();

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

    @Override
    public List<BookResponse> getBookListByCategory(String category) {
        Category categoryEnum;

        try {
            categoryEnum = Category.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            // 잘못된 카테고리인 경우 처리 (예외 던지거나 빈 리스트 반환)
            return List.of(); // 빈 리스트 반환 예시
        }

        List<Book> bookList = bookRepository.findAllByCategoryAndSecretFalse(categoryEnum);
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