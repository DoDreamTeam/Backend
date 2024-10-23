package com.dodream.mypage.service;

import com.dodream.book.entity.BookComment;
import com.dodream.mypage.domain.BookCommentLikeResponse;
import com.dodream.mypage.domain.BookCommentResponse;
import com.dodream.mypage.domain.BookUpdateResponse;
import com.dodream.mypage.domain.UserInfoResponse;
import java.util.List;


public interface MyPageBookService {

    // 사용자 정보 가져오기 (userName , profileImage , userBooks )
    UserInfoResponse getUserInfoAll();

    // 문제집 공개 비공개 설정
    BookUpdateResponse updateSecret(Long bookId);

    // 마이페이지 문제집 댓글 목록 조회
    List<BookCommentResponse> getUserComment();

    // 문제집 댓글 좋아요 목록 조회
    List<BookCommentLikeResponse> getUserCommentLike();
}
