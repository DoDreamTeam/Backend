package com.dodream.study.controller;

import com.dodream.study.domain.NoticeCommentLikeResponse;
import com.dodream.study.service.NoticeCommentLikeService;
import com.dodream.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notice/")
@RequiredArgsConstructor
public class NoticeCommentLikeController {

    private final NoticeCommentLikeService noticeCommentLikeService;

    @PostMapping("{id}/comments/{commentId}/like")
    public ResponseEntity<NoticeCommentLikeResponse> toggleNoticeCommentLike(
        @AuthenticationPrincipal User user, @PathVariable("commentId") Long commentId) {
        NoticeCommentLikeResponse noticeCommentLike =
            noticeCommentLikeService.toggleNoticeCommentLike(user, commentId);
        return ResponseEntity.ok(noticeCommentLike);
    }

}
