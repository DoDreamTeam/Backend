package com.dodream.study.domain;

import lombok.Data;

@Data
public class UpdateNoticeRequest {
    private String content;
    private boolean isDeleted;
}
