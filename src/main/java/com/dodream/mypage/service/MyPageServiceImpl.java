package com.dodream.mypage.service;

import com.dodream.book.domain.BookResponse;
import com.dodream.book.entity.Book;
import com.dodream.book.entity.UserBook;
import com.dodream.book.repository.BookmarkRepository;
import com.dodream.book.repository.UserBookRepository;
import com.dodream.common.exception.BaseException;
import com.dodream.common.exception.ErrorCode;
import com.dodream.mypage.domain.UserInfoResponse;
import com.dodream.user.entity.User;
import com.dodream.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
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
            .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        return UserInfoResponse.toProfileDTO(user);
    }

    // 사용자의 문제집 목록 조회
    @Override
    public Page<BookResponse> getUserBooks(Long userId, Pageable pageable) {
        // 사용자의 문제집 리스트
        Page<UserBook> userBooksPage = userBookRepository
            .findByUserIdAndBookSecretFalseOrderByBookCreatedAtDesc(
            userId, pageable);

        // 문제집 정보 반환
        List<BookResponse> bookResponses = userBooksPage.getContent().stream()
            .map(userBook -> {
                Book book = userBook.getBook();
                return BookResponse.builder()
                    .id(book.getId())
                    .title(book.getTitle())
                    .username(book.getUser() != null ? book.getUser().getUsername() : null)
                    .bookmarkCount(bookmarkRepository.countByBookAndIsDeletedFalse(book))
                    .category(book.getCategory().name())
                    .createdAt(book.getCreatedAt())
                    .build();
            })
            .toList();
        return new PageImpl<>(bookResponses, pageable, userBooksPage.getTotalElements());
    }


    // 사용자 프로필 수정
    @Override
    public UserInfoResponse updateUserProfile(String newUserName, String newProfileImage) {
        User loginuser = (User) SecurityContextHolder.getContext().getAuthentication()
            .getPrincipal();
        Long loginUserId = loginuser.getId();

        User user = userRepository.findById(loginUserId)
            .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        // 유저네임과 프로필 이미지 수정
        if (newUserName != null) {
            user.setUsername(newUserName);
        }
        if (newProfileImage != null) {
            user.setProfileImage(newProfileImage);
        }
        userRepository.save(user);
        return UserInfoResponse.toProfileDTO(user);
    }
}