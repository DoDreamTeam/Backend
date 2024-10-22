package com.dodream.mypage.domain;

import lombok.Data;

@Data
public class BookUpdateRequest {
    private String title;
    private String category;
}
