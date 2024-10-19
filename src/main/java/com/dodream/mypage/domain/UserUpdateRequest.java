package com.dodream.mypage.domain;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String username;
    private String profileImage;
}
