package com.dodream.notifications.enumtype;

import lombok.Getter;

@Getter
public enum NotifyType {

    // 문제집 댓글 추가
    BOOK_COMMENT("BOOK_COMMENT"),

    // 문제 댓글 추가
    QUESTION_COMMENT("QUESTION_COMMENT"),

    // 스터디 가입 신청
    STUDY_APPLY("STUDY_APPLY"),

    // 스터디 가입 승인
    STUDY_APPROVAL("STUDY_APPROVAL"),

    // 스터디 가입 거절
    STUDY_REFUSAL("STUDY_REFUSAL"),

    // 스터디 방장 변경
    LEADER_CHANGE("LEADER_CHANGE"),

    // 스터디 멤버 탈퇴
    STUDY_MEMBER_WITHDRAW("STUDY_MEMBER_WITHDRAW");

    String notifyType;

    NotifyType(String notifyType) {
        this.notifyType = notifyType;
    }
}
