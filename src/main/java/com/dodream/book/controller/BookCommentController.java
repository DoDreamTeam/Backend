package com.dodream.book.controller;

import com.dodream.book.domain.BookCommentResponse;
import com.dodream.book.service.BookCommentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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

    // 댓글 수정

    // 댓글 삭제

}