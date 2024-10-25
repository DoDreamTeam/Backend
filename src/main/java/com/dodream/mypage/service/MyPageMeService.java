package com.dodream.mypage.service;

import com.dodream.book.domain.BookResponse;
import com.dodream.book.domain.BookUpdateResponse;
import com.dodream.mypage.domain.BookCommentLikeResponse;
import com.dodream.mypage.domain.BookCommentResponse;
import com.dodream.mypage.domain.GetUserAnswerResponse;
import com.dodream.mypage.domain.QueCommentLikeResponse;
import com.dodream.mypage.domain.QueCommentResponse;
import com.dodream.mypage.domain.UserInfoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface MyPageMeService {

    // 사용자 정보 가져오기 (userName , profileImage)
    UserInfoResponse getUserProfile();

    // 사용자 문제집 목록 가져오기
    Page<BookResponse> getUserBooksAll(Pageable pageable);

    // 북마크한 문제집 목록 가져오기
    Page<BookResponse> getUserBookmarks(Pageable pageable);

    // 문제집 공개 비공개 설정
    BookUpdateResponse updateSecret(Long bookId);

    // 문제집 댓글 목록 조회
    Page<BookCommentResponse> getUserComment(Pageable pageable);

    // 문제집 댓글 좋아요 목록 조회
    Page<BookCommentLikeResponse> getUserCommentLike(Pageable pageable);

    // 내가 푼 문제 목록 조회 (전체)
    Page<GetUserAnswerResponse> getUserAnswer(Pageable pageable);

    // 내가 푼 문제 목록 조회 (애매해요)
    Page<GetUserAnswerResponse> getUserAnswerByEvaluation(String evaluation, Pageable pageable);

    // 스터디 댓글 목록 조회
    Page<QueCommentResponse> getStudyComment(Pageable pageable);

    // 스터디 댓글 좋아요 목록 조회
    Page<QueCommentLikeResponse> getStudyCommentLike(Pageable pageable);
}
