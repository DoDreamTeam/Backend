package com.dodream.book.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkResponse {

    private Long id;
    private boolean isDeleted;
    private Long userId;
    private Long bookId;

}
