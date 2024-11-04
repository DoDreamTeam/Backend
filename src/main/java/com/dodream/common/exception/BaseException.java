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
    public static final BaseException BOOK_NOT_FOUND = new BaseException(ErrorCode.BOOK_NOT_FOUND);
    public static final BaseException STUDY_CATEGORY_ERROR = new BaseException(ErrorCode.STUDY_CATEGORY_ERROR);
    public static final BaseException STUDY_CATEGORY_NOT_FOUND = new BaseException(ErrorCode.STUDY_CATEGORY_NOT_FOUND);
    public static final BaseException STUDY_SEARCH_NOT_FOUND = new BaseException(ErrorCode.STUDY_SEARCH_NOT_FOUND);
    public static final BaseException INTERNAL_SERVER_ERROR = new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
    public static final BaseException ACCESS_DENIED = new BaseException(ErrorCode.ACCESS_DENIED);
    public static final BaseException INVALID_TOKEN = new BaseException(ErrorCode.INVALID_TOKEN);
    public static final BaseException USER_NOT_FOUND = new BaseException(ErrorCode.USER_NOT_FOUND);
    public static final BaseException BOOK_ID_NOT_FOUND = new BaseException(ErrorCode.BOOK_ID_NOT_FOUND);
    public static final BaseException BOOK_COMMENT_NOT_FOUND = new BaseException(ErrorCode.BOOK_COMMENT_NOT_FOUND);
    public static final BaseException COMMENT_LIKE_NOT_FOUND = new BaseException(ErrorCode.COMMENT_LIKE_NOT_FOUND);
    public static final BaseException USER_ANSWER_NOT_FOUND = new BaseException(ErrorCode.USER_ANSWER_NOT_FOUND);
    public static final BaseException USER_ANSWER_EVALUATION_ERROR = new BaseException(ErrorCode.USER_ANSWER_EVALUATION_ERROR);
    public static final BaseException QUESTION_NOT_FOUND = new BaseException(ErrorCode.QUESTION_NOT_FOUND);
    public static final BaseException STUDY_USER_ANSWER_NOT_FOUND = new BaseException(ErrorCode.STUDY_USER_ANSWER_NOT_FOUND);
    public static final BaseException STUDY_NOT_FOUND = new BaseException(ErrorCode.STUDY_NOT_FOUND);
    public static final BaseException QUE_COMMENT_NOT_FOUND = new BaseException(ErrorCode.QUE_COMMENT_NOT_FOUND);
    public static final BaseException NOTICE_NOT_FOUND = new BaseException(ErrorCode.NOTICE_NOT_FOUND);
    public static final BaseException NOTICE_COMMENT_NOT_FOUND = new BaseException(ErrorCode.NOTICE_COMMENT_NOT_FOUND);
    public static final BaseException INVALID_EVALUATION = new BaseException(ErrorCode.INVALID_EVALUATION);
    public static final BaseException ANSWER_NOT_FOUND = new BaseException(ErrorCode.ANSWER_NOT_FOUND);
    public static final BaseException ALREADY_APPLIED_TO_STUDY = new BaseException(ErrorCode.ALREADY_APPLIED_TO_STUDY);
    public static final BaseException STUDY_MEMBER_NOT_FOUND = new BaseException(ErrorCode.STUDY_MEMBER_NOT_FOUND);
    public static final BaseException INVALID_CURRENT_LEADER = new BaseException(ErrorCode.INVALID_CURRENT_LEADER);
    public static final BaseException INVALID_NEW_LEADER = new BaseException(ErrorCode.INVALID_NEW_LEADER);
    public static final BaseException FILE_DELETE_FAILED = new BaseException(ErrorCode.FILE_DELETE_FAILED);
    public static final BaseException LEADER_NOT_FOUND = new BaseException(ErrorCode.LEADER_NOT_FOUND);
    public static final BaseException NOTIFICATION_NOT_FOUND = new BaseException(ErrorCode.NOTIFICATION_NOT_FOUND);

    private final ErrorCode errorCode;

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this; // 스택 트레이스 생략
    }

    public HttpStatus getHttpStatus() {
        return errorCode.getHttpStatus();
    }
}