package com.dodream.mypage.controller;


import com.dodream.book.domain.BookResponse;
import com.dodream.book.domain.BookUpdateResponse;
import com.dodream.mypage.domain.BookCommentLikeResponse;
import com.dodream.mypage.domain.BookCommentResponse;
import com.dodream.mypage.domain.GetUserAnswerResponse;
import com.dodream.mypage.domain.QueCommentLikeResponse;
import com.dodream.mypage.domain.QueCommentResponse;
import com.dodream.mypage.domain.UserInfoResponse;
import com.dodream.mypage.service.MyPageMeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage/book")
public class MyPageMeController {

    private final MyPageMeService myPageBookService;

    // 사용자 정보 가져오기 (userName , profileImage , userBooks)
    @GetMapping("/profile")
    public ResponseEntity<UserInfoResponse> getUserProfile() {
        UserInfoResponse userProfile = myPageBookService.getUserProfile();
        return ResponseEntity.ok(userProfile);
    }

    // 문제집 목록가져오기
    @GetMapping("/books")
    public ResponseEntity<Page<BookResponse>> getUserBooks(
        @PageableDefault(page = 0, size = 4) Pageable pageable) {
        Page<BookResponse> userBooks = myPageBookService.getUserBooksAll(pageable);
        return ResponseEntity.ok(userBooks);
    }

    // 북마크 문제집 목록 가져오기
    @GetMapping("/bookmarks")
    public ResponseEntity<Page<BookResponse>> getUserBookmarks(
        @PageableDefault(page = 0, size = 4) Pageable pageable
    ) {
        Page<BookResponse> userBookMarks = myPageBookService.getUserBookmarks(pageable);
        return ResponseEntity.ok(userBookMarks);
    }

    // 문제집 공개 비공개 설정
    @PatchMapping("/{id}/secret")
    public ResponseEntity<BookUpdateResponse> updateSecret(@PathVariable("id") Long id) {
        BookUpdateResponse updateSecret = myPageBookService.updateSecret(id);
        return ResponseEntity.ok(updateSecret);
    }

    // 사용자의 문제집 댓글 목록 조회
    @GetMapping("/comment")
    public ResponseEntity<Page<BookCommentResponse>> getBookComment(
        @PageableDefault(page = 0, size = 4) Pageable pageable
    ) {
        Page<BookCommentResponse> getUserComment = myPageBookService.getUserComment(pageable);
        return ResponseEntity.ok(getUserComment);
    }

    // 사용자의 문제집 댓글 좋아요 목록 조회
    @GetMapping("/comment/like")
    public ResponseEntity<Page<BookCommentLikeResponse>> getBookCommentLike(
        @PageableDefault(page = 0, size = 4) Pageable pageable
    ) {
        Page<BookCommentLikeResponse> getUserCommentLike = myPageBookService.getUserCommentLike(
            pageable);
        return ResponseEntity.ok(getUserCommentLike);
    }

    // 사용자가 푼 문제 목록 (전체)
    @GetMapping("/answer")
    public ResponseEntity<Page<GetUserAnswerResponse>> getUserAnswer(
        @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        Page<GetUserAnswerResponse> getUserAnswer = myPageBookService.getUserAnswer(pageable);
        return ResponseEntity.ok(getUserAnswer);
    }

    // 사용자가 푼 문제 목록 (평가별)
    @GetMapping("/answer/evaluation")
    public ResponseEntity<Page<GetUserAnswerResponse>> getUserAnswerEvaluation(
        @RequestParam(value = "evaluation") String evaluation,
        @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        Page<GetUserAnswerResponse> userAnswer = myPageBookService.getUserAnswerByEvaluation(
            evaluation, pageable);
        return ResponseEntity.ok(userAnswer);
    }

    // 사용자의 스터디 문제 댓글 목록 조회
    @GetMapping("/comment/study")
    public ResponseEntity<Page<QueCommentResponse>> getStudyComment(
        @PageableDefault(page = 0, size = 4) Pageable pageable
    ) {
        Page<QueCommentResponse> getStudyComment = myPageBookService.getStudyComment(pageable);
        return ResponseEntity.ok(getStudyComment);
    }

    // 사용자의 스터디 문제 댓글 좋아요 목록 조회
    @GetMapping("/comment/study/like")
    public ResponseEntity<Page<QueCommentLikeResponse>> getStudyCommentLike(
        @PageableDefault(page = 0, size = 4) Pageable pageable
    ) {
        Page<QueCommentLikeResponse> getStudyCommentLike = myPageBookService.getStudyCommentLike(
            pageable);
        return ResponseEntity.ok(getStudyCommentLike);
    }
}
