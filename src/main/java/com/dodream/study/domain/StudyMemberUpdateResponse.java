package com.dodream.study.domain;

import com.dodream.study.enumtype.RoleEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudyMemberUpdateResponse {

    private Long id;
    private RoleEnum roleEnum;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime joinDate;

}
