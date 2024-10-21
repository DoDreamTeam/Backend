package com.dodream.mypage.service;

import com.dodream.mypage.domain.BookUpdateRequest;
import com.dodream.mypage.domain.BookUpdateResponse;
import com.dodream.mypage.domain.UserInfoResponse;


public interface MyPageBookService {

    // 사용자 정보 가져오기 (userName , profileImage , userBooks )
    UserInfoResponse getUserInfoAll(Long userId);

    // 문제집 제목 수정
    BookUpdateResponse updateBook(Long bookId,BookUpdateRequest request);
}
