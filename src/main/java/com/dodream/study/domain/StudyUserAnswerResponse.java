package com.dodream.study.domain;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudyUserAnswerResponse {
    private Long id;
    private String question;
    private String modelAnswer;
    private String answer;
    private LocalDateTime createdAt;
    private String profileImage;
}
