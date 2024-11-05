package com.dodream.study.domain;

import com.dodream.common.enumtype.Category;
import com.dodream.study.entity.Study;
import com.dodream.user.entity.User;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudyRequest {
    private String title;
    private String username;
    private Category category;
    private String description;

    @JsonCreator
    public StudyRequest(
        @JsonProperty("title") String title,
        @JsonProperty("username") String username,
        @JsonProperty("category") Category category,
        @JsonProperty("description") String description
    ) {
        this.title = title;
        this.username = username;
        this.category = category;
        this.description = description;
    }
    public Study toEntity(User user) {
        return Study.builder()
            .title(title)
            .description(description)
            .user(user)
            .category(category)
            .build();
    }

}
