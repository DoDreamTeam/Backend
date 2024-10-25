package com.dodream.study.domain;

import com.dodream.study.enumtype.RoleEnum;
import lombok.Data;

@Data
public class StudyMemberRequest {
    private RoleEnum roleEnum;
}
