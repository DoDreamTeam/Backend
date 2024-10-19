package com.dodream.mypage.controller;


import com.dodream.mypage.domain.UserInfoResponse;
import com.dodream.mypage.domain.UserUpdateRequest;
import com.dodream.mypage.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/mypage")
public class MyPageController {
    private final MyPageService myPageService;

    // 사용자 정보 가져오기 (userName , profileImage , userBooks)
    @GetMapping("/{id}")
    public ResponseEntity<UserInfoResponse> getUserInfo(@PathVariable("id") Long id) {
        UserInfoResponse userInfo = myPageService.getUserInfo(id);
        return ResponseEntity.ok(userInfo);
    }

    // 사용자 프로필 수정 (userName , profileImage)
    @PatchMapping("/{id}")
    public ResponseEntity<UserInfoResponse> updateUserInfo(@PathVariable("id") Long id,
                                                           @RequestBody UserUpdateRequest userUpdateRequest) {
        UserInfoResponse updateUserProfile = myPageService.updateUserProfile(id, userUpdateRequest.getUsername(),
                userUpdateRequest.getProfileImage());
        return ResponseEntity.ok(updateUserProfile);
    }

}
