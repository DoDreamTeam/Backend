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
    private List<BookResponse> userBooks; // 유저 문제집
    private List<BookResponse> bookmarks; // 북마크한 문제집

    // 사용자 정보 조회 (북마크 포함)
    public static UserInfoResponse toDTO(User user, List<BookResponse> userBooks, List<BookResponse> bookmarks) {
        return UserInfoResponse.builder()
            .userId(user.getId())
            .userName(user.getUsername())
            .profileImage(user.getProfileImage())
            .userBooks(userBooks)
            .bookmarks(bookmarks)
            .build();
    }

    // 사용자 정보 조회 (북마크 제외)
    public static UserInfoResponse toDTO(User user, List<BookResponse> userBooks) {
        return UserInfoResponse.builder()
            .userId(user.getId())
            .userName(user.getUsername())
            .profileImage(user.getProfileImage())
            .userBooks(userBooks)
            .bookmarks(List.of()) // 빈 리스트로 북마크
            .build();
    }

    // 프로필 수정
    public static UserInfoResponse toDTO(User user) {
        return UserInfoResponse.builder()
            .userId(user.getId())
            .userName(user.getUsername())
            .profileImage(user.getProfileImage())
            .build();
    }
}
