package com.dodream.book.controller;

import com.dodream.book.domain.BookCommentRequest;
import com.dodream.book.domain.BookCommentResponse;
import com.dodream.book.domain.BookCommentUpdateRequest;
import com.dodream.book.domain.BookCommentUpdateResponse;
import com.dodream.book.service.BookCommentService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookCommentController {

    private final BookCommentService bookCommentService;

    // 댓글 조회
    @GetMapping("/{id}/comments")
    public ResponseEntity<List<BookCommentResponse>> getAllComments(@PathVariable("id") Long id) {
        List<BookCommentResponse> bookCommentList = bookCommentService.getCommentList(id);
        return ResponseEntity.ok(bookCommentList);
    }

    // 댓글 생성
    @PostMapping("/{id}/comments")
    public ResponseEntity<BookCommentResponse> createComment(@PathVariable("id") Long id,
        @AuthenticationPrincipal User user, @RequestBody BookCommentRequest bookCommentRequest) {
        BookCommentResponse savedComment = bookCommentService.addComment(id, user, bookCommentRequest);
        return ResponseEntity.ok(savedComment);
    }

    // 댓글 수정
    @PatchMapping("/{id}/comments/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable("id") Long id, @PathVariable("commentId") Long commentId,
        @RequestBody BookCommentUpdateRequest bookCommentUpdateRequest, @AuthenticationPrincipal User user) {
        BookCommentUpdateResponse updatedComment = bookCommentService.updateComment(commentId, user, bookCommentUpdateRequest);
        return ResponseEntity.ok(updatedComment);
    }

    // 댓글 삭제
    @DeleteMapping("/{id}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable("id") Long id, @PathVariable("commentId") Long commentId
        , @AuthenticationPrincipal User user) {
        bookCommentService.deleteComment(commentId, user);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

}