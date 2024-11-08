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
    private Long userId;            // 생성자 ID
    private String profileImage;     // 생성한 사람 프로필

    // 문제집의 경우에만 필요한 필드
    private String category;     // 문제집의 카테고리
    private Long bookmarkCount;      // 북마크 수
    private boolean isBookmarked;   // 사용자가 북마크했는지 여부

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
        this.userId = bookResponse.getUserId();
        this.profileImage = bookResponse.getUserProfile();
        this.isBookmarked = bookResponse.isBookmarked();
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
        this.userId = studyResponse.getUserId();
        this.profileImage = studyResponse.getProfileImage();
    }
}