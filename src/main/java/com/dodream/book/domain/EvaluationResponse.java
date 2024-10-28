package com.dodream.book.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EvaluationResponse {
    private String evaluationType; // 평가 유형
    private Long userId;           // 사용자 ID
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt; // 생성 시간

    public EvaluationResponse(String evaluationType, Long userId, LocalDateTime createdAt) {
        this.evaluationType = evaluationType;
        this.userId = userId;
        this.createdAt = createdAt;
    }
}