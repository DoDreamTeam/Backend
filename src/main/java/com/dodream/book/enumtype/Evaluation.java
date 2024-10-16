package com.dodream.book.enumtype;

public enum Evaluation {

    EVALUATION_BEFORE("EVALUATION_BEFORE"),
    EVALUATION_DONE("EVALUATION_DONE"),
    EVALUATION_SOSO("EVALUATION_SOSO"),
    EVALUATION_UNKNOWN("EVALUATION_UNKNOWN");

    String evaluation;

    Evaluation(String evaluation) {
        this.evaluation = evaluation;
    }

    public String getEvaluation() {
        return evaluation;
    }
}
