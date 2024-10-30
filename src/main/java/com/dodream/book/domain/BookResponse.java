package com.dodream.book.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookResponse {
    private Long id;
    private String title;           // 문제집 제목
    private String username;        // 생성한 사람
    private Long bookmarkCount;      // 북마크 수
    private String category;        // 카테고리
    private boolean secret;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;    // 생성한 날짜 (정렬을 위해 필요)
}
