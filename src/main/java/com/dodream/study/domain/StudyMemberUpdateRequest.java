package com.dodream.study.domain;

import com.dodream.study.enumtype.RoleEnum;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudyMemberUpdateRequest {

    private Long id;
    private RoleEnum role;

}
