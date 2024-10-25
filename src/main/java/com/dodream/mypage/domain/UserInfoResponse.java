package com.dodream.mypage.domain;

import com.dodream.book.domain.BookResponse;
import com.dodream.user.entity.User;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfoResponse {

    private Long userId; // 유저 ID
    private String userName; // 유저 이름
    private String profileImage; // 유저 프로필사진

    // 사용자 프로필 정보
    public static UserInfoResponse toProfileDTO(User user) {
        return UserInfoResponse.builder()
            .userId(user.getId())
            .userName(user.getUsername())
            .profileImage(user.getProfileImage())
            .build();
    }
}
