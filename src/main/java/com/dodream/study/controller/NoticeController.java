package com.dodream.study.controller;

import com.dodream.study.domain.NoticeRequest;
import com.dodream.study.domain.NoticeResponse;
import com.dodream.study.domain.UpdateNoticeRequest;
import com.dodream.study.domain.UpdateNoticeResponse;
import com.dodream.study.service.NoticeService;
import com.dodream.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notice")
public class NoticeController {

    private final NoticeService noticeService;

    // 공지사항 추가
    @PostMapping("/study/{noticeId}")
    public ResponseEntity<NoticeResponse> createNotice(
        @PathVariable Long noticeId,
        @RequestBody NoticeRequest noticeRequest,
        @AuthenticationPrincipal User user) {
        NoticeResponse noticeResponse = noticeService.createNotice(noticeId, noticeRequest, user);
        return ResponseEntity.ok(noticeResponse);
    }

    // 공지사항 수정
    @PutMapping("/{noticeId}")
    public ResponseEntity<UpdateNoticeResponse> updateNotice(
        @PathVariable Long noticeId, @AuthenticationPrincipal User user,
        @RequestBody UpdateNoticeRequest updateNoticeRequest) {
        UpdateNoticeResponse updateNoticeResponse
            = noticeService.updateNotice(noticeId, updateNoticeRequest, user);
        return ResponseEntity.ok(updateNoticeResponse);
    }

    // 공지사항 삭제
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<UpdateNoticeResponse> deleteNotice(
        @PathVariable Long noticeId, @AuthenticationPrincipal User user) {
        UpdateNoticeResponse updateNoticeResponse = noticeService.deleteNotice(noticeId, user);
        return ResponseEntity.ok(updateNoticeResponse);
    }

    // 공지사항 조회
    @GetMapping("/study/{studyId}/notice/{noticeId}")
    public ResponseEntity<NoticeResponse> getNoticeByStudyIdAndNoticeId(
        @PathVariable Long studyId, @PathVariable Long noticeId) {
        NoticeResponse noticeResponse = noticeService.getNoticeByStudyIdAndNoticeId(studyId,
            noticeId);
        return ResponseEntity.ok(noticeResponse);
    }

}

