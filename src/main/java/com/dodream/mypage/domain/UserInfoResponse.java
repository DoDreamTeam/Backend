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

    public static UserInfoResponse toDTO(User user, List<BookResponse> userBooks) {
        return UserInfoResponse.builder().userId(user.getId()).userName(user.getUsername())
            .profileImage(user.getProfileImage()).userBooks(userBooks).build();
    }
}
