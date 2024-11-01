package com.dodream.book.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookCommentUpdateResponse {
    private Long userId;            // 작성자 ID
    private String comment;         // 댓글
}

