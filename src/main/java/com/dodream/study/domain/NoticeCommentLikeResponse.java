package com.dodream.study.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeCommentLikeResponse {
    private Long id;
    private boolean isDeleted;
    private Long userId;
    private Long noticeCommentId;
}
