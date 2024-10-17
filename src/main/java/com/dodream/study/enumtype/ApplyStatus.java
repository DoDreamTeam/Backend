package com.dodream.study.enumtype;

import lombok.Getter;

// 신청, 승인대기중, 참여중
// APPLY, APPLY_WAITING, APPLY_DONE
@Getter
public enum ApplyStatus {
    APPLY("APPLY"),
    APPLY_WAITING("APPLY_WAITING"),
    APPLY_DONE("APPLY_DONE");

    final String status;

    ApplyStatus(String status) {
        this.status = status;
    }

}
