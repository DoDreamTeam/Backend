package com.dodream.book.service;

import com.dodream.book.domain.BookmarkResponse;
import com.dodream.book.entity.Book;
import com.dodream.book.entity.Bookmark;
import com.dodream.book.repository.BookRepository;
import com.dodream.book.repository.BookmarkRepository;
import com.dodream.common.exception.BaseException;
import com.dodream.common.exception.ErrorCode;
import com.dodream.user.entity.User;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final BookRepository bookRepository;

    @Override
    @Transactional
    public BookmarkResponse toggleBookmark(User user, Long id) {
        // 북마크할 문제집
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new BaseException(ErrorCode.BOOK_NOT_FOUND));

        // 북마크 조회
        Optional<Bookmark> existingBookmark = bookmarkRepository.findByUserAndBook(user, book);
        BookmarkResponse bookmarkResponse;

        if (existingBookmark.isPresent()) {
            // 기존 북마크가 존재하는 경우 (북마크 취소)
            Bookmark bookmark = existingBookmark.get();
            boolean newIsDeleted = !bookmark.isDeleted(); // 새로운 isDeleted 값 계산

            // 업데이트된 북마크 저장
            bookmarkRepository.save(Bookmark.builder()
                .id(bookmark.getId())
                .isDeleted(newIsDeleted)
                .user(bookmark.getUser())
                .book(bookmark.getBook())
                .build());

            // 응답 객체 생성
            bookmarkResponse = BookmarkResponse.builder()
                .id(bookmark.getId())
                .isDeleted(newIsDeleted)
                .userId(user.getId())
                .bookId(book.getId())
                .build();

        } else {
            // 새로운 북마크 생성
            Bookmark newBookmark = Bookmark.builder()
                .user(user)
                .book(book)
                .isDeleted(false) // 기본값은 false
                .build();

            // 새로운 북마크 저장
            bookmarkRepository.save(newBookmark);

            // 응답 객체 생성
            bookmarkResponse = BookmarkResponse.builder()
                .id(newBookmark.getId())
                .isDeleted(false)
                .userId(user.getId())
                .bookId(book.getId())
                .build();
        }

        return bookmarkResponse;
    }

}