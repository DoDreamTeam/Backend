package com.dodream.book.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookCommentUpdateResponse {
    private String comment;
}

