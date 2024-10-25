package com.dodream.mypage.service;

import com.dodream.book.domain.BookResponse;
import com.dodream.mypage.domain.UserInfoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface MyPageService {

    // 사용자 정보 가져오기 (userName , profileImage)
    UserInfoResponse getUserInfo(Long userId);

    // 사용자 문제집 목록 가져오기 (전체)
    Page<BookResponse> getUserBooks(Long userId, Pageable pageable);

    // 사용자 프로필 수정하기
    UserInfoResponse updateUserProfile(String newUserName, String newProfileImage);

}
