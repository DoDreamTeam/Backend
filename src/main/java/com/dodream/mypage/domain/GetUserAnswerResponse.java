package com.dodream.mypage.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetUserAnswerResponse {

    private Long id;
    private String title;
    private String evaluation;
    private LocalDateTime createdAt;
}
