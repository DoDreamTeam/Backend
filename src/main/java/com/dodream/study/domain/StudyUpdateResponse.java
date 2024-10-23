package com.dodream.study.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudyUpdateResponse {

    private Long id;
    private String title;
    private String description;

}
