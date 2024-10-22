package com.dodream.mypage.controller;


import com.dodream.mypage.domain.BookUpdateResponse;
import com.dodream.mypage.domain.UserInfoResponse;
import com.dodream.mypage.service.MyPageBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage/book")
public class MyPageBookController {

    private final MyPageBookService myPageBookService;

    // 사용자 정보 가져오기 (userName , profileImage , userBooks)
    @GetMapping("/{id}")
    public ResponseEntity<UserInfoResponse> getUserInfoAll(@PathVariable("id") Long id) {
        UserInfoResponse userInfo = myPageBookService.getUserInfoAll(id);
        return ResponseEntity.ok(userInfo);
    }

    // 문제집 공개 비공개 설정
    @PatchMapping("/{id}/secret")
    public ResponseEntity<BookUpdateResponse> updateSecret(@PathVariable("id") Long id) {
        BookUpdateResponse updateSecret = myPageBookService.updateSecret(id);
        return ResponseEntity.ok(updateSecret);
    }
}
