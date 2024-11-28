package com.dodream.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    /* 400 */
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "입력값 유효성 검사에 실패했습니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "INVALID_TOKEN", "유효하지 않은 토큰입니다."),
    BOOK_NOT_FOUND(HttpStatus.BAD_REQUEST, "BOOK_NOT_FOUND", "해당 id를 가진 문제집이 없습니다."),
    INVALID_EVALUATION(HttpStatus.BAD_REQUEST, "INVALID_EVALUATION", "유효하지 않은 평가입니다"),
    INVALID_CURRENT_LEADER(HttpStatus.BAD_REQUEST, "INVALID_CURRENT_LEADER", "INVALID_CURRENT_LEADER - 검증되지 않은 값입니다."),
    INVALID_NEW_LEADER(HttpStatus.BAD_REQUEST, "INVALID_NEW_LEADER", "INVALID_NEW_LEADER - 검증되지 않은 값입니다."),
    ALREADY_APPLIED_TO_STUDY(HttpStatus.BAD_REQUEST, "ALREADY_APPLIED_TO_STUDY", "이미 가입신청이 되었습니다."),
    ALREADY_STUDY_MEMBER(HttpStatus.BAD_REQUEST, "ALREADY_STUDY_MEMBER", "이미 해당 스터디 멤버입니다."),
    FILE_DELETE_FAILED(HttpStatus.BAD_REQUEST, "FILE_DELETE_FAILED", "파일 삭제에 실패했습니다."),

    /* 401 */
    AUTHENTICATION_FAILED(HttpStatus.BAD_REQUEST, "AUTHENTICATION_FAILED", "인증에 실패했습니다"),

    /* 403 */
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "접근 권한이 없습니다."),

    /* 404 */
    BOOK_CATEGORY_ERROR(HttpStatus.NOT_FOUND, "BOOK_CATEGORY_ERROR", "해당 카테고리는 존재하지 않습니다"),
    BOOK_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "BOOK_CATEGORY_NOT_FOUND", "해당 카테고리에 포함된 문제집이 없습니다."),
    BOOK_SEARCH_NOT_FOUND(HttpStatus.NOT_FOUND, "BOOK_SEARCH_NOT_FOUND", "검색어와 일치하는 문제집이 없습니다."),
    STUDY_CATEGORY_ERROR(HttpStatus.NOT_FOUND, "STUDY_CATEGORY_ERROR", "해당 카테고리는 존재하지 않습니다"),
    STUDY_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "STUDY_CATEGORY_NOT_FOUND", "해당 카테고리에 포함된 스터디가 없습니다."),
    STUDY_SEARCH_NOT_FOUND(HttpStatus.NOT_FOUND, "STUDY_SEARCH_NOT_FOUND", "검색어와 일치하는 스터디가 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "사용자를 찿을 수 없습니다."),
    BOOK_ID_NOT_FOUND(HttpStatus.NOT_FOUND, "BOOK_ID_NOT_FOUND", "해당 id를 가진 문제집이 존재하지 않습니다."),
    BOOK_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "BOOK_COMMENT_NOT_FOUND", "댓글이 존재하지 않습니다"),
    COMMENT_LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT_LIKE_NOT_FOUND", "좋아요 한 댓글이 존재하지 않습니다"),
    USER_ANSWER_EVALUATION_ERROR(HttpStatus.NOT_FOUND, "USER_ANSWER_EVALUATION_ERROR", "해당 평가는 존재하지 않습니다"),
    USER_ANSWER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_ANSWER_NOT_FOUND", "사용자가 푼 문제가 존재하지 않습니다"),
    QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "QUESTION_NOT_FOUND", "해당 id를 가진 문제가 존재하지 않습니다."),
    STUDY_USER_ANSWER_NOT_FOUND(HttpStatus.NOT_FOUND, "STUDY_USER_ANSWER_NOT_FOUND",
        "스터디에 추가한 사용자 문제가 존재하지 않습니다"),
    NOTICE_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTICE_COMMENT_NOT_FOUND", "공지사항 댓글이 존재하지 않습니다."),
    ANSWER_NOT_FOUND(HttpStatus.NOT_FOUND, "ANSWER_NOT_FOUND", "해당 id를 가진 답안이 존재하지 않습니다"),

    QUE_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "QUE_COMMENT_NOT_FOUND", "댓글이 존재하지 않습니다"),

    STUDY_NOT_FOUND(HttpStatus.NOT_FOUND, "STUDY_NOT_FOUND", "해당 id를 가진 스터디가 없습니다."),
    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTICE_NOT_FOUND", "해당 id를 가진 공지사항이 없습니다."),
    STUDY_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "STUDY_MEMBER_NOT_FOUND", "해당 스터디 멤버를 찾을 수 없습니다."),

    LEADER_NOT_FOUND(HttpStatus.NOT_FOUND, "LEADER_NOT_FOUND", "스터디 방장을 찾을 수 없습니다"),
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTIFICATION_NOT_FOUND", "해당 알림을 찾을 수 없습니다"),

    /* 500 */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "예상치 못한 서버 에러가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}