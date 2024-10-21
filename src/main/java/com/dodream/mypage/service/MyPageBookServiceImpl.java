package com.dodream.mypage.service;

import com.dodream.book.domain.BookResponse;
import com.dodream.book.entity.Book;
import com.dodream.book.entity.UserBook;
import com.dodream.book.repository.BookRepository;
import com.dodream.book.repository.BookmarkRepository;
import com.dodream.book.repository.UserBookRepository;
import com.dodream.mypage.domain.BookUpdateRequest;
import com.dodream.mypage.domain.BookUpdateResponse;
import com.dodream.mypage.domain.UserInfoResponse;
import com.dodream.user.entity.User;
import com.dodream.user.repository.UserRepository;
import java.util.List;
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
            .orElseThrow(() -> new IllegalArgumentException("회원 정보가 없습니다"));

        // 사용자의 문제집 리스트
        List<UserBook> userBooks = userBookRepository.findByUserId(userId);

        userBooks.forEach(userBook -> System.out.println(userBook.getBook().getTitle()));

        User loginuser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long loginUserId = loginuser.getId();

        if(userId.equals(loginUserId)) {
            throw new SecurityException("조회 권한이 없습니다");
        }

        // 사용자 문제집의 정보 리스트
        List<BookResponse> books = userBooks.stream()
            .map(UserBook::getBook)
            .distinct()
            .map(book -> BookResponse.builder()
                .id(book.getId()).title(book.getTitle())
                .username(book.getUser() != null ? book.getUser().getUsername() : null)
                .bookmarkCount(bookmarkRepository.countByBookAndIsDeletedFalse(book))
                .category(book.getCategory().name())
                .createdAt(book.getCreatedAt())
                .build())
            .toList();

        return UserInfoResponse.toDTO(user, books);
    }

    // 문제집 수정
    @Override
    public BookUpdateResponse updateBook(Long bookId, BookUpdateRequest request) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new IllegalArgumentException("문제집이 없습니다"));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long loginUserId = user.getId();

        if(!book.getUser().getId().equals(loginUserId)) {
            throw new SecurityException("수정 권한이 없습니다");
        }

        if (request.getTitle() != null) {
            book.setTitle(request.getTitle());
        }

        bookRepository.save(book);

        return BookUpdateResponse
            .builder()
            .id(book.getId())
            .title(book.getTitle())
            .build();
    }
}
