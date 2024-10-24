package com.dodream.study.domain;

import com.dodream.study.entity.Notice;
import com.dodream.study.entity.Study;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
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
