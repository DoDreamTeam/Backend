package com.dodream.study.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NoticeCommentUpdateResponse {
    private String content;
}
