package com.dodream.book.repository;

import com.dodream.book.entity.Book;
import com.dodream.book.entity.Bookmark;
import com.dodream.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Integer> {

    // 특정 문제집의 북마크 수 count (is_deleted = false 인 경우에만)
    long countByBookAndIsDeletedFalse(Book book);

    // 북마크하기
    Optional<Bookmark> findByUserAndBook(User user, Book book);
}
