package com.dodream.book.repository;

import com.dodream.book.entity.Book;
import com.dodream.book.entity.Bookmark;
import com.dodream.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Integer> {

    // 특정 문제집의 북마크 수 count (is_deleted = false 인 경우에만)
    long countByBookAndIsDeletedFalse(Book book);

    // 북마크하기
    Optional<Bookmark> findByUserAndBook(User user, Book book);

    // 유저 북마크 목록
    Page<Bookmark> findByUserIdOrderByBookCreatedAtDesc(Long userId, Pageable pageable);
}
