package com.dodream.book.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnswerDetailsResponse {
    private String question;   // 문제 내용
    private String modelAnswer; // 모범답안
    private String userAnswer;  // 사용자가 작성한 답안
}