package com.dodream.book.controller;

import com.dodream.book.domain.BookCommentLikeResponse;
import com.dodream.book.service.BookCommentLikeService;
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
public class BookCommentLikeController {
    private final BookCommentLikeService bookCommentLikeService;

    @PostMapping("/{id}/comments/{commentId}/like")
    public ResponseEntity<BookCommentLikeResponse> toggleCommentLike(@AuthenticationPrincipal User user, @PathVariable("id") Long id,
        @PathVariable("commentId") Long commentId) {
        BookCommentLikeResponse commentLike = bookCommentLikeService.toggleCommentLike(user, commentId);
        return ResponseEntity.ok(commentLike);
    }

}