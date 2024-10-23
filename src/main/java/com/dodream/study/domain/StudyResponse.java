package com.dodream.study.domain;

import com.dodream.common.enumtype.Category;
import com.dodream.study.entity.Study;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudyResponse {
    private Long id;
    private String title;           // 2024년 정처기 실기 2회 대비
    private String username;        // test
    private String description;     // 스터디에 대한 아주 간단한 설명
    private Category category;        // 자격증
    private String status;          // 신청, 승인대기중, 참여중

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private Long userCount;             // 멤버 수 -> StudyMember Id row 개수 (Member, Leader 포함)

    public StudyResponse(Study study) {
        this.id = study.getId();
        this.title = study.getTitle();
        this.username = study.getUser().getUsername();
        this.description = study.getDescription();
        this.category = study.getCategory();
        this.updatedAt = study.getUpdatedAt();
        this.createdAt = study.getCreatedAt();
    }

}
