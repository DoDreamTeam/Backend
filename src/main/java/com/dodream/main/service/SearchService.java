package com.dodream.main.service;

import com.dodream.main.domain.SearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchService {
    Page<SearchResponse> searchByKeyword(String keyword, Pageable pageable);
}
