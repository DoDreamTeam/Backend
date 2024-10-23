package com.dodream.mypage.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookCommentLikeResponse {
    private Long commentId;
    private String comment;
    private Long userId;
    private String userName;
    private LocalDateTime createdAt;
}
