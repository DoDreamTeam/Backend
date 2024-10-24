package com.dodream.mypage.controller;


import com.dodream.book.domain.BookUpdateResponse;
import com.dodream.book.entity.BookComment;
import com.dodream.mypage.domain.BookCommentLikeResponse;
import com.dodream.mypage.domain.BookCommentResponse;
import com.dodream.mypage.domain.UserInfoResponse;
import com.dodream.mypage.service.MyPageBookService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage/book")
public class MyPageBookController {

    private final MyPageBookService myPageBookService;

    // 사용자 정보 가져오기 (userName , profileImage , userBooks)
    @GetMapping("")
    public ResponseEntity<UserInfoResponse> getUserInfoAll() {
        UserInfoResponse userInfo = myPageBookService.getUserInfoAll();
        return ResponseEntity.ok(userInfo);
    }

    // 문제집 공개 비공개 설정
    @PatchMapping("/{id}/secret")
    public ResponseEntity<BookUpdateResponse> updateSecret(@PathVariable("id") Long id) {
        BookUpdateResponse updateSecret = myPageBookService.updateSecret(id);
        return ResponseEntity.ok(updateSecret);
    }

    // 사용자의 문제집 댓글 목록 조회
    @GetMapping("/comment")
    public ResponseEntity<List<BookCommentResponse>> getBookComment() {
        List<BookCommentResponse> getUserComment = myPageBookService.getUserComment();
        return ResponseEntity.ok(getUserComment);
    }

    // 사용자의 문제집 댓글 좋아요 목록 조회
    @GetMapping("/comment/like")
    public ResponseEntity<List<BookCommentLikeResponse>> getBookCommentLike() {
        List<BookCommentLikeResponse> getUserCommentLike = myPageBookService.getUserCommentLike();
        return ResponseEntity.ok(getUserCommentLike);
    }


}
