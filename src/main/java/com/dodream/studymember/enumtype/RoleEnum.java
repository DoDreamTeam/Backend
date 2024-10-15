package com.dodream.studymember.enumtype;

import lombok.Getter;

@Getter
public enum RoleEnum {

    // MEMBER, LEADER, WAITING
    ROLE_MEMBER("ROLE_MEMBER"),
    ROLE_LEADER("ROLE_LEADER"),
    ROLE_WAITING("ROLE_WAITING");

    String role;

    RoleEnum(String role) {
        this.role = role;
    }

}
