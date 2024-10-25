package com.dodream.mypage.controller;


import com.dodream.book.domain.BookResponse;
import com.dodream.mypage.domain.UserInfoResponse;
import com.dodream.mypage.domain.UserUpdateRequest;
import com.dodream.mypage.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MyPageController {

    private final MyPageService myPageService;

    // 사용자 정보 가져오기 (userName , profileImage , userBooks)
    @GetMapping("/{id}")
    public ResponseEntity<UserInfoResponse> getUserInfo(@PathVariable("id") Long id) {
        UserInfoResponse userInfo = myPageService.getUserInfo(id);
        return ResponseEntity.ok(userInfo);
    }

    // 사용자 문제집 목록 가져오기
    @GetMapping("/books/{id}")
    public ResponseEntity<Page<BookResponse>> getUserBook(
        @PathVariable("id") Long userId,
        @PageableDefault(page = 0, size = 4)
        Pageable pageable) {
        Page<BookResponse> userBooks = myPageService.getUserBooks(userId, pageable);
        return ResponseEntity.ok(userBooks);
    }

    // 사용자 프로필 수정 (userName , profileImage)
    @PatchMapping("")
    public ResponseEntity<UserInfoResponse> updateUserInfo(
        @RequestBody UserUpdateRequest userUpdateRequest) {
        UserInfoResponse updateUserProfile = myPageService.updateUserProfile(
            userUpdateRequest.getUsername(),
            userUpdateRequest.getProfileImage());
        return ResponseEntity.ok(updateUserProfile);
    }

}
