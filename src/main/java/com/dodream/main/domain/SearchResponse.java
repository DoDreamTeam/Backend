package com.dodream.main.domain;

import com.dodream.book.domain.BookResponse;
import com.dodream.study.domain.StudyResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SearchResponse {
    private Long id;            // ID
    private String title;       // 검색 결과 제목
    private String username;    // 생성한 사용자
    private String type;        // "Book" 또는 "Study"

    // 문제집의 경우에만 필요한 필드
    private Long bookmarkCount; // 문제집의 북마크 수
    private String category;     // 문제집의 카테고리

    // 스터디의 경우에만 필요한 필드
    private String description;  // 스터디 설명
    private Long userCount;      // 스터디 멤버 수
    private String status;       // 스터디 상태

    public SearchResponse(BookResponse bookResponse) {
        this.id = bookResponse.getId();
        this.title = bookResponse.getTitle();
        this.username = bookResponse.getUsername();
        this.type = "Book";
        this.bookmarkCount = bookResponse.getBookmarkCount();
        this.category = bookResponse.getCategory();
    }

    public SearchResponse(StudyResponse studyResponse) {
        this.id = studyResponse.getId();
        this.title = studyResponse.getTitle();
        this.username = studyResponse.getUsername();
        this.type = "Study";
        this.description = studyResponse.getDescription();
        this.userCount = studyResponse.getUserCount();
        this.status = studyResponse.getStatus();
        this.category = studyResponse.getCategory() != null ? studyResponse.getCategory().name() : null; // 필요한 경우 카테고리 변환
    }
}