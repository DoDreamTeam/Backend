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
public class QueCommentLikeResponse {

    private Long commentId; // 댓글 ID
    private String comment; // 댓글 내용
    private Long userId; // 사용자 ID
    private String userName; // 사용자 이름
    private LocalDateTime createdAt; // 생성날짜
    private Long studyAnswerId;
    private Long studyId;
}