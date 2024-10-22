package com.dodream.mypage.service;

import com.dodream.book.domain.BookRequest;
import com.dodream.book.domain.BookResponse;
import com.dodream.mypage.domain.BookUpdateRequest;
import com.dodream.mypage.domain.BookUpdateResponse;
import com.dodream.mypage.domain.UserInfoResponse;


public interface MyPageBookService {

    // 사용자 정보 가져오기 (userName , profileImage , userBooks )
    UserInfoResponse getUserInfoAll(Long userId);

    // 문제집 수정
    BookUpdateResponse updateBook(Long bookId, BookUpdateRequest request);

    // 문제집 삭제
    BookResponse deleteBook(Long bookId);
}
