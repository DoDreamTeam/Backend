package com.dodream.mypage.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookUpdateResponse {
    private Long id;
    private String title; // 문제집 제목
}
