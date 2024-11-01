package com.dodream.study.controller;

import com.dodream.study.domain.NoticeCommentRequest;
import com.dodream.study.domain.NoticeCommentResponse;
import com.dodream.study.domain.NoticeCommentUpdateRequest;
import com.dodream.study.domain.NoticeCommentUpdateResponse;
import com.dodream.study.service.NoticeCommentService;
import com.dodream.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
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
@RequestMapping("/api/notice")
@RequiredArgsConstructor
public class NoticeCommentController {

    private final NoticeCommentService noticeCommentService;

    // 공지사항 댓글 조회 (최신순 / 좋아요 순으로 정렬)
    @GetMapping("/{id}/comments")
    public ResponseEntity<Page<NoticeCommentResponse>> getAllNoticeComments(
        @PageableDefault(page = 0, size = 5, sort = "createdAt",
        direction = Direction.DESC) Pageable pageable,
        @AuthenticationPrincipal User user,
        @PathVariable("id") Long id,
        @RequestParam(value = "sortByLikes", required = false) boolean isSortByLikes) {
        Page<NoticeCommentResponse> noticeCommentList
            = noticeCommentService.getNoticeCommentList(pageable, id, user, isSortByLikes);
        return ResponseEntity.ok(noticeCommentList);
    }


    // 공지사항 댓글 생성
    @PostMapping("/{id}/comments")
    public ResponseEntity<NoticeCommentResponse> createNoticeComment(@PathVariable("id") Long id,
        @AuthenticationPrincipal User user, @RequestBody NoticeCommentRequest noticeCommentRequest) {
        NoticeCommentResponse noticeComment =
            noticeCommentService.addNoticeComment(id, user, noticeCommentRequest);
        return ResponseEntity.ok(noticeComment);
    }

    // 공지사항 댓글 삭제
    @DeleteMapping("/{id}/comments/{commentId}")
    public ResponseEntity<NoticeCommentResponse> deleteNoticeComment(
        @PathVariable("commentId") Long commentId,
        @AuthenticationPrincipal User user) {
        noticeCommentService.deleteNoticeComment(commentId, user);
        return ResponseEntity.noContent().build();
    }

    // 공지사항 댓글 수정
    @PatchMapping("/{id}/comments/{commentId}")
    public ResponseEntity<NoticeCommentUpdateResponse> updateNoticeComment(
        @PathVariable("commentId") Long commentId,
        @RequestBody NoticeCommentUpdateRequest noticeCommentUpdateRequest,
        @AuthenticationPrincipal User user) {
        NoticeCommentUpdateResponse updatedNoticeComment =
            noticeCommentService.updateNoticeComment(commentId, user, noticeCommentUpdateRequest);
        return ResponseEntity.ok(updatedNoticeComment);
    }

}
