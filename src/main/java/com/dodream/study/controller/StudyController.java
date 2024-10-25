package com.dodream.study.controller;

import com.dodream.study.domain.NoticeRequest;
import com.dodream.study.domain.StudyMemberResponse;
import com.dodream.study.domain.StudyRequest;
import com.dodream.study.domain.StudyResponse;
import com.dodream.study.domain.StudyUpdateRequest;
import com.dodream.study.domain.StudyUpdateResponse;
import com.dodream.study.entity.StudyMember;
import com.dodream.study.service.StudyService;
import com.dodream.user.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
@RequiredArgsConstructor
@RequestMapping("/api/study")
public class StudyController {

    private final StudyService studyService;

    // 스터디 전체 조회 (최신순 조회 + 카테고리별 전체 조회)
    @GetMapping("")
    public ResponseEntity<Page<StudyResponse>> getStudyList(
        @PageableDefault(page = 0, size = 12, sort = "createdAt",
            direction = Sort.Direction.DESC) Pageable pageable,
        @AuthenticationPrincipal User user,
        @RequestParam(value = "category", required = false) String category) {
        Page<StudyResponse> response = studyService.getStudyList(user, pageable, category);
        return ResponseEntity.ok(response);
    }

    // 인기 스터디 조회
    @GetMapping("/popular")
    public ResponseEntity<Page<StudyResponse>> getPopularStudyList(
        @PageableDefault(page = 0, size = 4, sort = "createdAt",
            direction = Sort.Direction.DESC) Pageable pageable,
        @AuthenticationPrincipal User user,
        @RequestParam(value = "sortByUserCount", required = false) Long userCount) {
        Page<StudyResponse> response = studyService.getPopularStudyList(pageable, user, userCount);
        return ResponseEntity.ok(response);
    }

    // 스터디 검색 조회 - updatedAt으로 DoDream에 수정
    @GetMapping("/search")
    public ResponseEntity<Page<StudyResponse>> searchStudies(
        @PageableDefault(page = 0, size = 12, sort = "updatedAt",
            direction = Sort.Direction.DESC) Pageable pageable,
        @RequestParam(value = "keyword", required = false) String keyword) {
        Page<StudyResponse> response = studyService.searchStudiesByKeyword(pageable, keyword);
        return ResponseEntity.ok(response);
    }

    // 내가 참여중인 스터디 조회 (나의 스터디 멤버 상태가 Member or Leader 인 경우)
    @GetMapping("/my")
    public ResponseEntity<Page<StudyResponse>> getMyStudy(
        @PageableDefault(page = 0, size = 5, sort = "joinDate",
            direction = Direction.DESC) Pageable pageable, @AuthenticationPrincipal User user) {
        Page<StudyResponse> response = studyService.getMyStudyList(pageable, user);
        return ResponseEntity.ok(response);
    }

    // 스터디 생성
    @PostMapping("")
    public ResponseEntity<StudyResponse> createStudy(@AuthenticationPrincipal User user,
        @RequestBody StudyRequest studyRequest) {
//        studyRequest.setUsername(user.getUsername());
        StudyResponse studyResponse = studyService.addStudy(user, studyRequest);
        return ResponseEntity.ok(studyResponse);
    }

    // 스터디 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<StudyResponse> deleteStudy(@AuthenticationPrincipal User user,
        @PathVariable("id") Long id) {
        studyService.deleteStudy(user, id);
        return ResponseEntity.noContent().build();
    }

    // 스터디 수정
    @PatchMapping("/{id}")
    public ResponseEntity<StudyUpdateResponse> updateStudy(@AuthenticationPrincipal User user,
        @PathVariable("id") Long id, @RequestBody StudyUpdateRequest studyUpdateRequest) {
        StudyUpdateResponse updateResponse = studyService.updateStudy(user, id ,studyUpdateRequest);
        return ResponseEntity.ok(updateResponse);
    }

}
