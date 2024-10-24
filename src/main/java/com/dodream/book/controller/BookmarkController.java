package com.dodream.book.controller;

import com.dodream.book.domain.BookmarkResponse;
import com.dodream.book.service.BookmarkService;
import com.dodream.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @PostMapping("/{id}/bookmark")
    public ResponseEntity<BookmarkResponse> addBookToBookmark(@AuthenticationPrincipal User user, @PathVariable("id") Long id) {
        BookmarkResponse bookmarkResponse = bookmarkService.toggleBookmark(user, id);
        return ResponseEntity.ok(bookmarkResponse);
    }

}