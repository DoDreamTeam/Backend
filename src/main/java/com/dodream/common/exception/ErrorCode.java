package com.dodream.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    /* 400 */
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "입력값 유효성 검사에 실패했습니다."),
    BOOK_NOT_FOUND(HttpStatus.BAD_REQUEST, "BOOK_NOT_FOUND", "해당 id를 가진 문제집이 없습니다."),
    USER_ALREADY_BOOKMARK(HttpStatus.BAD_REQUEST, "USER_ALREADY_BOOKMARK", "이미 북마크를 한 문제집입니다."),

    /* 403 */
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "접근 권한이 없습니다."),

    /* 404 */
    BOOK_CATEGORY_ERROR(HttpStatus.NOT_FOUND, "BOOK_CATEGORY_ERROR", "해당 카테고리는 존재하지 않습니다"),
    BOOK_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "BOOK_CATEGORY_NOT_FOUND", "해당 카테고리에 포함된 문제집이 없습니다."),
    BOOK_SEARCH_NOT_FOUND(HttpStatus.NOT_FOUND, "BOOK_SEARCH_NOT_FOUND", "검색어와 일치하는 문제집이 없습니다."),

    /* 500 */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "예상치 못한 서버 에러가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}