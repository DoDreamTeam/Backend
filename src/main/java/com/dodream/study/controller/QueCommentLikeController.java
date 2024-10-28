package com.dodream.study.controller;

import com.dodream.study.domain.QueCommentLikeResponse;
import com.dodream.study.service.QueCommentLikeService;
import com.dodream.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/study/answer")
@RequiredArgsConstructor
public class QueCommentLikeController {

    private final QueCommentLikeService queCommentLikeService;

    @PostMapping("{id}/comments/{commentId}/like")
    public ResponseEntity<QueCommentLikeResponse> toggleQueCommentLike(
        @AuthenticationPrincipal User user,
        @PathVariable("commentId") Long commentId) {
        QueCommentLikeResponse queCommentLike =
                queCommentLikeService.toggleQueCommentLike(user, commentId);
        return ResponseEntity.ok(queCommentLike);
    }
}
