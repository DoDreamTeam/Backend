package com.dodream.study.domain;

import com.dodream.common.enumtype.Category;
import com.dodream.study.entity.Study;
import com.dodream.user.entity.User;
import lombok.Data;

@Data
public class StudyRequest {
    private String title;
    private Category category;
    private String description;

    public Study toEntity(User user) {
        return Study.builder()
            .title(title)
            .description(description)
            .user(user)
            .category(category)
            .build();
    }

}
