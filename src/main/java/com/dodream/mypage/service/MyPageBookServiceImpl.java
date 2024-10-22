package com.dodream.mypage.service;

import com.dodream.book.domain.BookResponse;
import com.dodream.book.entity.Book;
import com.dodream.book.entity.Bookmark;
import com.dodream.book.entity.UserBook;
import com.dodream.book.repository.BookRepository;
import com.dodream.book.repository.BookmarkRepository;
import com.dodream.book.repository.UserBookRepository;
import com.dodream.common.exception.BaseException;
import com.dodream.common.exception.ErrorCode;
import com.dodream.mypage.domain.BookUpdateResponse;
import com.dodream.mypage.domain.UserInfoResponse;
import com.dodream.user.entity.User;
import com.dodream.user.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyPageBookServiceImpl implements MyPageBookService {

    private final UserRepository userRepository;
    private final UserBookRepository userBookRepository;
    private final BookmarkRepository bookmarkRepository;
    private final BookRepository bookRepository;

    // 사용자 정보 가져오기 (userName , profileImage , userBooks )
    @Override
    public UserInfoResponse getUserInfoAll(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        access(userId);

        // 사용자의 문제집 리스트
        List<UserBook> userBooks = userBookRepository.findByUserId(userId);
        List<Bookmark> userBookMarks = bookmarkRepository.findByUserId(userId);

        List<BookResponse> bookResponses = getBookResponse(userBooks, userBookMarks);

        return UserInfoResponse.toDTO(user, bookResponses);

    }

    private void access(Long userId) {
        User loginuser = (User) SecurityContextHolder.getContext().getAuthentication()
            .getPrincipal();
        if (!userId.equals(loginuser.getId())) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }
    }

    private List<BookResponse> getBookResponse(List<UserBook> userBooks, List<Bookmark> bookmarks) {
        return Stream.concat(
            getBookStream(userBooks.stream().map(UserBook::getBook)),
            getBookStream(bookmarks.stream().map(Bookmark::getBook))
        ).distinct().collect(Collectors.toList());
    }

    private Stream<BookResponse> getBookStream(Stream<Book> books) {
        return books.filter(book -> !book.isSecret())
            .map(book -> BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .username(book.getUser() != null ? book.getUser().getUsername() : null)
                .category(book.getCategory().name())
                .createdAt(book.getCreatedAt())
                .build());
    }

    // 문제집 공개 비공개 설정
    @Override
    public BookUpdateResponse updateSecret(Long bookId) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new BaseException(ErrorCode.BOOK_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        bookOwner(book, user);

        // 문제집 소유자 확인
        if (!book.getUser().getId().equals(user.getId())) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }

        book.setSecret(!book.isSecret());
        bookRepository.save(book);

        return BookUpdateResponse.builder()
            .id(book.getId())
            .secret(book.isSecret())
            .build();
    }

    private void bookOwner(Book book, User user) {
        if (!book.getUser().getId().equals(user.getId())) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }
    }
}
