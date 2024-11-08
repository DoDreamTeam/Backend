package com.dodream.study.domain;

import com.dodream.study.entity.Study;
import com.dodream.study.entity.StudyMember;
import com.dodream.study.enumtype.RoleEnum;
import com.dodream.user.entity.User;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class StudyMemberRequest {
    private Long studyId;
    private RoleEnum roleEnum;

    public StudyMember toEntity(User user, Study study) {
        return StudyMember.builder()
            .role(roleEnum)
            .study(study)
            .user(user)
            .joinDate(LocalDateTime.now())
            .build();
    }
}
