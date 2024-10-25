package com.dodream.book.domain;

import lombok.Data;

@Data
public class QuestionRequest {
    private String question;
    private String modelAnswer;
}
