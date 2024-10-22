package com.dodream.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class BaseException extends RuntimeException {

    public static final BaseException VALIDATION_FAILED = new BaseException(ErrorCode.VALIDATION_FAILED);
    public static final BaseException BOOK_CATEGORY_ERROR = new BaseException(ErrorCode.BOOK_CATEGORY_ERROR);
    public static final BaseException BOOK_CATEGORY_NOT_FOUND = new BaseException(ErrorCode.BOOK_CATEGORY_NOT_FOUND);
    public static final BaseException BOOK_SEARCH_NOT_FOUND = new BaseException(ErrorCode.BOOK_SEARCH_NOT_FOUND);
    public static final BaseException INTERNAL_SERVER_ERROR = new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
    public static final BaseException BOOK_NOT_FOUND = new BaseException(ErrorCode.BOOK_NOT_FOUND);
    public static final BaseException ACCESS_DENIED = new BaseException(ErrorCode.ACCESS_DENIED);

    private final ErrorCode errorCode;

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this; // 스택 트레이스 생략
    }

    public HttpStatus getHttpStatus() {
        return errorCode.getHttpStatus();
    }
}