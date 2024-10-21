package com.dodream.common.response;

import com.dodream.common.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

    @JsonProperty("errorCode")
    private String code;

    @JsonProperty("errorMessage")
    private String message;

    public static ErrorResponse from(ErrorCode errorCode) {
        return ErrorResponse
            .builder()
            .code(errorCode.getCode())
            .message(errorCode.getMessage())
            .build();
    }

    public void changeMessage(String message) {
        this.message = message;
    }

}