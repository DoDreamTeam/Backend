package com.dodream.user.domain;

import lombok.Getter;

@Getter
public enum AuthEnum {
    KAKAO("kakao"),
    GOOGLE("google"),
    NAVER("naver");

    final String auth;

    AuthEnum(String auth) {
        this.auth = auth;
    }

    public static AuthEnum fromString(String auth) {
        for (AuthEnum authEnum : AuthEnum.values()) {
            if (authEnum.getAuth().equalsIgnoreCase(auth)) {
                return authEnum;
            }
        }
        throw new IllegalArgumentException("없는 값: " + auth);
    }
}
