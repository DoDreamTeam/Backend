package com.dodream.book.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionResponse {
    private Long id;                    // 문제 ID
    private String question;            // 문제
    private String modelAnswer;         // 문제 모범답안
    private Long bookId;                // 문제가 포함된 문제집 ID

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;    // 생성 날짜
}
