package com.dodream.book.domain;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddToMyBooksResponse {
    private Long questionId;
    private List<Long> addedToBooks;
    private Long userId;
}