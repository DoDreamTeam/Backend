package com.dodream.study.controller;

import com.dodream.study.domain.StudyUserQueAnswerResponse;
import com.dodream.study.service.StudyUserQueAnswerService;
import com.dodream.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/study/{id}/studyroom")
@RequiredArgsConstructor
public class StudyUserQueAnswerController {

    private final StudyUserQueAnswerService studyUserQueAnswerService;

    // 스터디방 문제 전체 조회 (내 답안 제외 조회 + 내가 푼 문제 조회 - 로그인 사용자 기준)
    @GetMapping("")
    public ResponseEntity<Page<StudyUserQueAnswerResponse>> getStudyUserAnswer(
        @PageableDefault(page = 0, size = 10, sort = "createdAt",
        direction = Direction.DESC) Pageable pageable,
        @PathVariable("id") Long studyId,
        @AuthenticationPrincipal User user) {
        Page<StudyUserQueAnswerResponse> response
            = studyUserQueAnswerService.getStudyDetails(pageable, studyId, user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<Page<StudyUserQueAnswerResponse>> getStudyMyUserAnswer(
        @PageableDefault(page = 0, size = 10, sort = "createdAt",
            direction = Direction.DESC) Pageable pageable,
        @PathVariable("id") Long studyId,
        @AuthenticationPrincipal User user) {
        Page<StudyUserQueAnswerResponse> response
            = studyUserQueAnswerService.getStudyMyDetails(pageable, studyId, user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/other")
    public ResponseEntity<Page<StudyUserQueAnswerResponse>> getStudyOtherUserAnswer(
        @PageableDefault(page = 0, size = 10, sort = "createdAt",
            direction = Direction.DESC) Pageable pageable,
        @PathVariable("id") Long studyId,
        @AuthenticationPrincipal User user) {
        Page<StudyUserQueAnswerResponse> response
            = studyUserQueAnswerService.getStudyOtherDetails(pageable, studyId, user);
        return ResponseEntity.ok(response);
    }


    // 스터디방 문제 검색 조회 (제목 + 내용)
     @GetMapping("/search")
    public ResponseEntity<Page<StudyUserQueAnswerResponse>> searchStudyOtherUserAnswer(
         @PageableDefault(page = 0, size = 10, sort = "createdAt",
             direction = Direction.DESC) Pageable pageable,
         @PathVariable("id") Long studyId,
         @AuthenticationPrincipal User user, String keyword) {
         Page<StudyUserQueAnswerResponse> response
             = studyUserQueAnswerService.getSearchStudyUserAnswer(pageable, studyId, user, keyword);
         return ResponseEntity.ok(response);
     }

}
