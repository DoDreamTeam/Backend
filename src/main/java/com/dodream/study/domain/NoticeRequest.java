package com.dodream.study.domain;

import com.dodream.common.enumtype.Category;
import com.dodream.study.entity.Notice;
import com.dodream.study.entity.Study;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NoticeRequest {
    private String content;
    private boolean isDeleted;

    @JsonCreator
    public NoticeRequest(
        @JsonProperty("content") String content,
        @JsonProperty("isDeleted") boolean isDeleted
    ) {
        this.content = content;
        this.isDeleted = isDeleted;
    }

    public Notice toEntity(Study study) {
        return Notice.builder()
            .content(content)
            .isDeleted(isDeleted)
            .study(study)
            .build();
    }
}
