package com.dodream.mypage.controller;


import com.dodream.book.domain.BookResponse;
import com.dodream.mypage.domain.GetUserAnswerResponse;
import com.dodream.mypage.domain.UserInfoResponse;
import com.dodream.mypage.domain.UserUpdateRequest;
import com.dodream.mypage.service.MyPageService;
import java.io.IOException;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
        @RequestParam(value = "file", required = false) MultipartFile file,
        @RequestParam(value = "newUserName", required = false) String newUserName) throws IOException {
        UserInfoResponse updateUserProfile = myPageService.updateUserProfile(
            newUserName, file);
        return ResponseEntity.ok(updateUserProfile);
    }

    // 사용자 푼 문제 목록 가져오기
    @GetMapping("/answers/{id}")
    public ResponseEntity<List<GetUserAnswerResponse>> getUserAnswers(@PathVariable("id") Long userId) {
        List<GetUserAnswerResponse> userAnswers = myPageService.getUserIdAnswers(userId);
        return ResponseEntity.ok(userAnswers);
    }

}
