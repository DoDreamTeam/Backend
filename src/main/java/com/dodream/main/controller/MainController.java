package com.dodream.main.controller;

import com.dodream.main.domain.SearchResponse;
import com.dodream.main.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class MainController {
    private final SearchService searchService;

    // 문제집 제목 + 스터디 제목 으로 검색
    @GetMapping("")
    public ResponseEntity<Page<SearchResponse>> getSearchResult(
        @PageableDefault(page = 0, size = 12) Pageable pageable,
        @RequestParam(value = "keyword") String keyword) {
        Page<SearchResponse> searchResults = searchService.searchByKeyword(keyword, pageable);
        return ResponseEntity.ok(searchResults);
    }
}