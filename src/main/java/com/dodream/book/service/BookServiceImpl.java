package com.dodream.book.service;

import com.dodream.book.domain.BookRequest;
import com.dodream.book.domain.BookResponse;
import com.dodream.book.domain.BookUpdateRequest;
import com.dodream.book.domain.BookUpdateResponse;
import com.dodream.book.entity.Book;
import com.dodream.book.repository.BookCommentRepository;
import com.dodream.book.repository.BookRepository;
import com.dodream.book.repository.BookmarkRepository;
import com.dodream.common.enumtype.Category;
import com.dodream.common.exception.BaseException;
import com.dodream.common.exception.ErrorCode;
import com.dodream.user.entity.User;
import java.util.List;
import java.util.Optional;
import javax.swing.text.html.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookmarkRepository bookmarkRepository;
    private final BookCommentRepository bookCommentRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<BookResponse> getBooks(String category, Pageable pageable,
        boolean sortByBookmarks, User user) {
        if (category != null) {
            return getBookListByCategory(pageable, category, sortByBookmarks, user);
        } else {
            return getBookList(pageable, sortByBookmarks, user);
        }
    }

    // 문제집 전체 조회
    @Override
    @Transactional(readOnly = true)
    public Page<BookResponse> getBookList(Pageable pageable, boolean sortByBookmarks, User user) {
        Page<Book> bookPage;
        if (sortByBookmarks) {
            bookPage = bookRepository.findAllBySecretFalseOrderByBookmarkCount(pageable);
        } else {
            bookPage = bookRepository.findAllBySecretFalseOrderByCreatedAtDesc(pageable);
        }
        return convertToBookResponsePage(bookPage, user);
    }

    // 문제집 카테고리별 조회
    @Override
    @Transactional(readOnly = true)
    public Page<BookResponse> getBookListByCategory(Pageable pageable, String category,
            boolean sortByBookmarks, User user) {
        Category categoryEnum;
        try {
            categoryEnum = Category.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BaseException(ErrorCode.BOOK_CATEGORY_ERROR);
        }

        Page<Book> bookPage;
        if (sortByBookmarks) {
            bookPage = bookRepository.findAllByCategoryAndSecretFalseOrderByBookmarkCount(categoryEnum, pageable);
        } else {
            bookPage = bookRepository.findAllByCategoryAndSecretFalseOrderByCreatedAtDesc(categoryEnum, pageable);
        }

        if (bookPage.isEmpty()) {
            throw new BaseException(ErrorCode.BOOK_CATEGORY_NOT_FOUND);
        }
        return convertToBookResponsePage(bookPage, user);
    }

    // 문제집 북마크 많은 순 4개 조회
    @Override
    @Transactional(readOnly = true)
    public Page<BookResponse> getPopularBooks(User user) {
        Pageable pageable = PageRequest.of(0, 4);
        Page<Book> popularBooksPage = bookRepository.findAllBySecretFalseOrderByBookmarkCount(pageable);

        return convertToBookResponsePage(popularBooksPage, user);
    }

    // 문제집 개별 조회
    @Override
    public BookResponse getBook(Long id, User user) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new BaseException(ErrorCode.BOOK_NOT_FOUND));

        boolean isBookmarked = (user != null) && bookmarkRepository.existsByUserIdAndBookId(user.getId(), book.getId());

        return BookResponse.builder()
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
    }

    // 문제집 제목으로 검색
    @Override
    @Transactional
    public Page<BookResponse> searchBooksByKeyword(String keyword, Pageable pageable, User user) {
        Page<Book> bookList = bookRepository.findAllByTitleContainingAndSecretFalseOrderByCreatedAtDesc(keyword, pageable);

        // 해당 검색어와 일치하는 문제집이 없는 경우 예외처리
        if(bookList.isEmpty()) {
            throw new BaseException(ErrorCode.BOOK_SEARCH_NOT_FOUND);
        }

        return convertToBookResponsePage(bookList, user);
    }

    // 문제집 생성
    @Override
    @Transactional
    public BookResponse addBook(User user, BookRequest bookRequest) {
        Book book = bookRequest.toEntity(user);
        Book savedBook = bookRepository.save(book);

        return BookResponse.builder()
            .id(savedBook.getId())
            .title(savedBook.getTitle())
            .userId(book.getUser() != null ? book.getUser().getId() : null)
            .username(user.getUsername())  // 현재 로그인한 사용자 이름
            .userProfile(book.getUser() != null ? book.getUser().getProfileImage() : null)
            .bookmarkCount(0L)             // 초기 북마크 수
            .category(savedBook.getCategory().name())
            .createdAt(savedBook.getCreatedAt()) // 실제 생성된 날짜 사용
            .build();
    }

    // 문제집 제목 수정
    @Override
    @Transactional
    public BookUpdateResponse updateBook(User user, Long id, BookUpdateRequest request) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new BaseException(ErrorCode.BOOK_NOT_FOUND));

        // 문제집 소유자 확인
        if (!book.getUser().getId().equals(user.getId())) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }

        if (request.getTitle() != null) {
            book.setTitle(request.getTitle());
        }
        if (request.getCategory() != null) {
            book.setCategory(Category.valueOf(request.getCategory()));
        }

        bookRepository.save(book);

        return BookUpdateResponse
            .builder()
            .id(book.getId())
            .title(book.getTitle())
            .category(book.getCategory().name())
            .build();
    }

    // 문제집 삭제
    @Override
    @Transactional
    public void deleteBook(Long id, User user) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new BaseException(ErrorCode.BOOK_NOT_FOUND));

        // 문제집 소유자 확인
        if (!book.getUser().getId().equals(user.getId())) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }

        bookmarkRepository.deleteByBook(book);
        bookCommentRepository.deleteByBookId(book.getId());
        bookRepository.delete(book);
    }

    // 전체 조회할때 사용하는 List
    private Page<BookResponse> convertToBookResponsePage(Page<Book> bookPage, User user) {
        List<BookResponse> bookResponses = bookPage.getContent().stream()
            .map(book -> {
                boolean isBookmarked = (user != null) && bookmarkRepository.existsByUserIdAndBookId(user.getId(), book.getId());

                return BookResponse.builder()
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
            })
            .toList();

        return new PageImpl<>(bookResponses, bookPage.getPageable(), bookPage.getTotalElements());
    }

}
