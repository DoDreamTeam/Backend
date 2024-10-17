package com.dodream.mypage.service;

import com.dodream.book.domain.BookResponse;
import com.dodream.book.entity.UserBook;
import com.dodream.book.repository.BookmarkRepository;
import com.dodream.book.repository.UserBookRepository;
import com.dodream.mypage.domain.UserInfoResponse;
import com.dodream.user.entity.User;
import com.dodream.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyPageServiceImpl implements MyPageService {

    private final UserRepository userRepository;
    private final UserBookRepository userBookRepository;
    private final BookmarkRepository bookmarkRepository;

    // 사용자 정보 가져오기 (userName , profileImage , userBooks )
    @Override
    public UserInfoResponse getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("회원 정보가 없습니다"));

        // 사용자의 문제집 리스트
        List<UserBook> userBooks = userBookRepository.findByUserId(userId);

        userBooks.forEach(userBook -> System.out.println(userBook.getBook().getTitle()));

        // 사용자 문제집의 정보 리스트
        List<BookResponse> books = userBooks.stream()
            .map(UserBook::getBook)
            .filter(book -> !book.isSecret())
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
}
