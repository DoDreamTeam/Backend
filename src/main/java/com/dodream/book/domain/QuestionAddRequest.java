package com.dodream.book.domain;

import lombok.Data;

@Data
public class QuestionAddRequest {
    private String question;
    private String modelAnswer;
}
