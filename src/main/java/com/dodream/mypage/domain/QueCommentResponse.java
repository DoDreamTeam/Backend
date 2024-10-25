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
public class QueCommentResponse {
    private Long id; // 댓글 ID
    private Long studyAnswerId; // 스터디 문제 ID
    private String comment; // 댓글 내용
    private Long userId; // 사용자 ID
    private String username; // 사용자 이름
    private String studyTitle; // 스터디 제목
    private LocalDateTime createdAt; // 생성 날짜
    private LocalDateTime updatedAt; // 수정 날짜
}
