package com.dodream.book.controller;

import com.dodream.book.domain.BookRequest;
import com.dodream.book.domain.BookResponse;
import com.dodream.book.service.BookService;
import com.dodream.mypage.domain.BookUpdateRequest;
import com.dodream.mypage.domain.BookUpdateResponse;
import com.dodream.user.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    // 문제집 전체/카테고리별 조회
    @GetMapping("")
    public ResponseEntity<List<BookResponse>> getAllBooks(@RequestParam(value = "category", required = false) String category) {
        List<BookResponse> bookList = bookService.getBooks(category);
        return ResponseEntity.ok(bookList);
    }

    // 문제집 제목 수정
    @PatchMapping("/{id}")
    public ResponseEntity<BookUpdateResponse> updateBook(@AuthenticationPrincipal User user, @PathVariable("id") Long id,
        @RequestBody BookUpdateRequest bookUpdateRequest) {

        BookUpdateResponse updateBookTitle = bookService.updateBook(user, id, bookUpdateRequest);
        return ResponseEntity.ok(updateBookTitle);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BookResponse> deleteBook(@AuthenticationPrincipal User user, @PathVariable("id") Long id) {
        bookService.deleteBook(id, user);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // 문제집 생성
    @PostMapping("")
    public ResponseEntity<BookResponse> createBook(@AuthenticationPrincipal User user, @RequestBody BookRequest bookRequest) {
        bookRequest.setUsername(user.getUsername()); // user 가져오기
        BookResponse addedBook = bookService.addBook(user, bookRequest);
        return ResponseEntity.ok(addedBook);
    }

    // 문제집 제목으로 검색
    @GetMapping("/search")
    public ResponseEntity<List<BookResponse>> searchBooks(@RequestParam(value = "keyword", required = false) String keyword) {
        List<BookResponse> bookList = bookService.searchBooksByKeyword(keyword);
        return ResponseEntity.ok(bookList);
    }

}