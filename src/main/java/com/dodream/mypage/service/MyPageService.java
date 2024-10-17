package com.dodream.mypage.service;

import com.dodream.mypage.domain.UserInfoResponse;


public interface MyPageService {

    // 사용자 정보 가져오기 (userName , profileImage , userBooks )
    UserInfoResponse getUserInfo(Long userId);
}
