package com.dodream.mypage.controller;


import com.dodream.mypage.domain.UserInfoResponse;
import com.dodream.mypage.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/mypage")
public class MyPageController {
    private final MyPageService myPageService;

    // 사용자 정보 가져오기 (userName , profileImage , userBooks )
    @GetMapping("/{id}")
    public ResponseEntity<UserInfoResponse> getUserInfo(@PathVariable("id") Long id) {
        UserInfoResponse userInfo = myPageService.getUserInfo(id);
        return ResponseEntity.ok(userInfo);
    }

}
