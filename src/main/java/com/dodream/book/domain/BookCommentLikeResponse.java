package com.dodream.book.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookCommentLikeResponse {

    private Long id;
    private boolean isDeleted;
    private Long userId;
    private Long commentId;

}