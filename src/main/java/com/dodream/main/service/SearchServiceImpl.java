package com.dodream.main.service;

import com.dodream.book.domain.BookResponse;
import com.dodream.book.entity.Book;
import com.dodream.book.repository.BookmarkRepository;
import com.dodream.main.domain.SearchResponse;
import com.dodream.main.repository.MainRepository;
import com.dodream.study.domain.StudyResponse;
import com.dodream.study.entity.Study;
import com.dodream.user.entity.User;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final MainRepository mainRepository;
    private final BookmarkRepository bookmarkRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<SearchResponse> searchByKeyword(String keyword, Pageable pageable) {
        // 문제집 검색
        Page<Book> bookPage = mainRepository.findBooksByTitle(keyword, pageable);

        // 스터디 검색
        Page<Study> studyPage = mainRepository.findStudiesByTitle(keyword, pageable);

        // 결과를 결합
        List<SearchResponse> combinedResults = new ArrayList<>();

        // 문제집 결과 추가
        bookPage.getContent().forEach(book -> {
            // 문제집 작성자 정보
            User bookUser = book.getUser();

            boolean isBookmarked = (bookUser != null) && bookmarkRepository.existsByUserIdAndBookIdAndIsDeletedFalse(bookUser.getId(), book.getId());

            BookResponse bookResponse = BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .userId(book.getUser() != null ? book.getUser().getId() : null)
                .username(book.getUser() != null ? book.getUser().getUsername() : null)
                .userProfile(book.getUser() != null ? book.getUser().getProfileImage() : null)
                .bookmarkCount(bookmarkRepository.countByBookAndIsDeletedFalse(book))
                .category(book.getCategory().name())
                .createdAt(book.getCreatedAt())
                .isBookmarked(isBookmarked) // 북마크 여부 추가
                .build();
            combinedResults.add(new SearchResponse(bookResponse));
        });

        // 스터디 결과 추가
        studyPage.getContent().forEach(study -> {
            StudyResponse studyResponse = new StudyResponse(study);
            combinedResults.add(new SearchResponse(studyResponse));
        });

        // 반환할 Page 생성
        return new PageImpl<>(combinedResults, pageable, bookPage.getTotalElements() + studyPage.getTotalElements());
    }
}