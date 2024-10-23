package com.dodream.book.domain;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookCommentResponse {
    private Long id;
    private String comment;
    private String username;                // 작성자
    private Long likeCount;                 // 좋아요 수
    private Long bookId;                    // 문제집 ID
    private LocalDateTime createdAt;        // 생성 날짜 (최신순 정렬 위해 필요)
}