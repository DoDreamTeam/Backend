package com.dodream.study.domain;

import com.dodream.book.enumtype.Evaluation;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class StudyUserQueAnswerResponse {

    private Long questionId;
    private String question;
    private String modelAnswer;
    private String username;
    private String profileImage;
    private Evaluation evaluation;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

}
