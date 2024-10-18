package com.dodream.book.controller;

import com.dodream.book.domain.BookResponse;
import com.dodream.book.service.BookService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping("")
    public ResponseEntity<List<BookResponse>> getAllBooks(@RequestParam(value = "category", required = false) String category) {
        List<BookResponse> bookList = null;
        if (category != null) {
            // 카테고리별 조회
            bookList = bookService.getBookListByCategory(category);
        } else {
            // 전체 문제집 조회
            bookList = bookService.getBookList();
        }
        return ResponseEntity.ok(bookList);
    }

}