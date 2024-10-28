package com.dodream.book.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionListResponse {
    private Long id;                            // 문제 ID
    private String question;                    // 문제
    private EvaluationResponse evaluation;      // 사용자의 평가

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;    // 생성 날짜
}
