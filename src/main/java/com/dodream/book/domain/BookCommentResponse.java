package com.dodream.book.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookCommentResponse {
    private Long id;
    private String comment;
    private Long userId;                    // 작성자 ID
    private String username;                // 작성자
    private String userProfile;     // 생성한 사람 프로필
    private Long likeCount;                 // 좋아요 수
    private Long bookId;                    // 문제집 ID
    private boolean isLiked;   // 사용자가 북마크했는지 여부

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;        // 생성 날짜 (최신순 정렬 위해 필요)
}