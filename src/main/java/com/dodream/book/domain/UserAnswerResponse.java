package com.dodream.book.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserAnswerResponse {
    private Long id;                                                // answer ID
    private Long userId;                                            // user ID
    private Long questionId;                                        // question ID
    private String answer;                                          // 사용자 답안
    private String evaluation;  // 문제 푼 이후 default (UNKNOWN 상태)
}