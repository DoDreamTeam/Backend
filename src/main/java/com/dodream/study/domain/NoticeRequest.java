package com.dodream.study.domain;

import com.dodream.study.entity.Notice;
import com.dodream.study.entity.Study;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NoticeRequest {
    private String content;
    private boolean isDeleted;
    public Notice toEntity(Study study) {
        return Notice.builder()
            .content(content)
            .isDeleted(isDeleted)
            .study(study)
            .build();
    }
}
