package com.dodream.study.controller;

import com.dodream.study.domain.StudyMemberRequest;
import com.dodream.study.domain.StudyMemberResponse;
import com.dodream.study.domain.StudyMemberUpdateRequest;
import com.dodream.study.domain.StudyMemberUpdateResponse;
import com.dodream.study.service.StudyMemberService;
import com.dodream.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    // 스터디 가입 신청 - 승인 대기 상태
    @PostMapping("")
    public ResponseEntity<StudyMemberResponse> createStudyMember(
        @AuthenticationPrincipal User user, @RequestBody StudyMemberRequest studyMemberRequest) {
        StudyMemberResponse studyMemberResponse =
            studyMemberService.addStudyMember(user, studyMemberRequest);
        return ResponseEntity.ok(studyMemberResponse);
    }


    // 스터디 멤버 권한 수정 - ROLE_WAITING 상태 => ROLE_MEMBER 로 업데이트
    @PatchMapping("/{memberId}")
    public ResponseEntity<StudyMemberUpdateResponse> updateStudyMember(
        @AuthenticationPrincipal User user,
        @PathVariable("memberId") Long memberId,
        @RequestBody StudyMemberUpdateRequest studyMemberUpdateRequest) {
        StudyMemberUpdateResponse studyMemberUpdateResponse =
            studyMemberService.updateStudyMember(user, memberId, studyMemberUpdateRequest);
        return ResponseEntity.ok(studyMemberUpdateResponse);
    }

    // 스터디 멤버 삭제 (회원 탈퇴, 가입 신청 인원 삭제 - 알림 전송 시 메세지 구분)
    @DeleteMapping("/{memberId}")
    public ResponseEntity<StudyMemberResponse> deleteStudyMember(@AuthenticationPrincipal User user,
        @PathVariable("memberId") Long memberId) {
        studyMemberService.deleteStudyMember(user, memberId);
        return ResponseEntity.noContent().build();
    }

    // 스터디 방장 변경 (기존 사용자 권한의 ROLE_LEADER를 ROLE_MEMBER 로 변경)
    // 변경된 방장의 권한을 ROLE_MEMBER에서 ROLE_LEADER로 변경
    @PatchMapping("/leader/{newLeaderId}")
    public ResponseEntity<StudyMemberUpdateResponse> updateMemberLeader(
        @AuthenticationPrincipal User user,
        @PathVariable("newLeaderId") Long newLeaderId) {
        StudyMemberUpdateResponse studyMemberUpdateResponse =
            studyMemberService.transferLeader(user, newLeaderId);
        return ResponseEntity.ok(studyMemberUpdateResponse);
    }

}
