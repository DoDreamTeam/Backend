package com.dodream.study.controller;

import com.dodream.study.domain.StudyMemberResponse;
import com.dodream.study.service.StudyMemberService;
import com.dodream.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/study/{id}/members")
public class StudyMemberController {

    private final StudyMemberService studyMemberService;

    // 스터디 멤버 조회 (ROLE_MEMBER 만 조회)
    @GetMapping("")
    public ResponseEntity<Page<StudyMemberResponse>> getStudyMembers(
        @PageableDefault(page = 0, size = 5, sort = "joinDate",
            direction = Sort.Direction.DESC) Pageable pageable,
        @AuthenticationPrincipal User user, @PathVariable("id") Long studyId) {
        Page<StudyMemberResponse> memberResponse = studyMemberService.getStudyMembers(studyId, user, pageable);
        return ResponseEntity.ok(memberResponse);
    }

    // 스터디 멤버 조회 (가입 신청 - ROLE_WAITING 만 조회)
    @GetMapping("/application")
    public ResponseEntity<Page<StudyMemberResponse>> getStudyApplyMembers(
        @PageableDefault(page = 0, size = 5, sort = "joinDate",
            direction = Sort.Direction.DESC) Pageable pageable,
        @AuthenticationPrincipal User user, @PathVariable("id") Long studyId) {
        Page<StudyMemberResponse> memberResponse = studyMemberService.getStudyApplyMembers(studyId, user, pageable);
        return ResponseEntity.ok(memberResponse);
    }

    // 스터디 가입 신청
//    @PostMapping("")
//    public ResponseEntity<Page<StudyMemberResponse>> createStudyMember(
//        @AuthenticationPrincipal User user
//    )




    // 스터디 방장 변경 (기존 사용자 권한의 ROLE_LEADER를 ROLE_MEMBER 로 변경)
    // 변경된 방장의 권한을 ROLE_MEMBER에서 ROLE_LEADER로 변경

}
