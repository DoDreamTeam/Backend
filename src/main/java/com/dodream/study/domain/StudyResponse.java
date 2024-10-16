package com.dodream.study.domain;

import com.dodream.study.entity.Study;
import com.dodream.user.entity.User;
import java.util.Locale.Category;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudyResponse {
    private Long id;
    private String title;           // 2024년 정처기 실기 2회 대비
    private String username;        // test
    private String description;     // 스터디에 대한 아주 간단한 설명
    private String category;        // 자격증
    private String status;          // 신청, 승인대기중, 참여중
}
