package com.dodream.study.controller;

import com.dodream.study.domain.QueCommentRequest;
import com.dodream.study.domain.QueCommentResponse;
import com.dodream.study.domain.QueCommentUpdateRequest;
import com.dodream.study.domain.QueCommentUpdateResponse;
import com.dodream.study.service.QueCommentService;
import com.dodream.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/study/answer")
@RequiredArgsConstructor
public class QueCommentController {
    private final QueCommentService queCommentService;

    // 스터디 문제 댓글 조회 (최신순 / 좋아요 순으로 정렬)
    @GetMapping("/{id}/comments")
    public ResponseEntity<Page<QueCommentResponse>> getAllQueComments(
            @PageableDefault(page = 0, size = 5, sort = "createdAt",
                    direction = Direction.DESC) Pageable pageable,
            @PathVariable("id") Long id,
        @AuthenticationPrincipal User user,
            @RequestParam(value = "sortByLikes", required = false) boolean isSortByLikes) {
        Page<QueCommentResponse> queCommentList
                = queCommentService.getQueCommentList(pageable, id, user, isSortByLikes);
        return ResponseEntity.ok(queCommentList);
    }

    // 스터디 문제 댓글 생성
    @PostMapping("/{id}/comments")
    public ResponseEntity<QueCommentResponse> createQueComment(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal User user, @RequestBody QueCommentRequest queCommentRequest) {
        QueCommentResponse queComment =
                queCommentService.addQueComment(id, user, queCommentRequest);
        return ResponseEntity.ok(queComment);
    }

    // 스터디 문제 댓글 삭제
    @DeleteMapping("{id}/comments/{commentId}")
    public ResponseEntity<QueCommentResponse> deleteQueComment(
        @PathVariable("commentId") Long commentId,
        @AuthenticationPrincipal User user) {
        queCommentService.deleteQueComment(commentId, user);
        return ResponseEntity.ok().build();
    }

    // 공지사항 댓글 수정
    @PatchMapping("/{id}/comments/{commentId}")
    public ResponseEntity<QueCommentUpdateResponse> updateQueComment(
        @PathVariable("commentId") Long commentId,
        @RequestBody QueCommentUpdateRequest queCommentUpdateRequest,
        @AuthenticationPrincipal User user) {
        QueCommentUpdateResponse updateQueComment =
                queCommentService.updateQueComment(commentId, user, queCommentUpdateRequest);
        return ResponseEntity.ok(updateQueComment);
    }
}

