package com.dodream.book.repository;

import com.dodream.book.entity.Book;
import com.dodream.common.enumtype.Category;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // 공개 문제집 전체 북마크 순으로 조회
    @Query("SELECT b FROM Book b LEFT JOIN Bookmark bm ON b.id = bm.book.id AND bm.isDeleted = false GROUP BY b ORDER BY COUNT(bm.id) DESC")
    Page<Book> findAllBySecretFalseOrderByBookmarkCount(Pageable pageable);

    // 공개 문제집 전체 최신순 조회
    Page<Book> findAllBySecretFalseOrderByCreatedAtDesc(Pageable pageable);

    // 공개 문제집 카테고리별로 전체 북마크 순으로 조회
    @Query("SELECT b FROM Book b LEFT JOIN Bookmark bm ON b.id = bm.book.id AND bm.isDeleted = false WHERE b.category = :category GROUP BY b ORDER BY COUNT(bm.id) DESC")
    Page<Book> findAllByCategoryAndSecretFalseOrderByBookmarkCount(@Param("category") Category category, Pageable pageable);

    // 공개 문제집 카테고리별로 전체 최신순 조회
    Page<Book> findAllByCategoryAndSecretFalseOrderByCreatedAtDesc(Category category, Pageable pageable);

    // 문제집 제목으로 검색 (공개 문제집만 검색 가능) 최신순으로 정렬
    Page<Book> findAllByTitleContainingAndSecretFalseOrderByCreatedAtDesc(String title, Pageable pageable);

    Page<Book> findByUserIdAndSecretFalseOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Page<Book> findByUserIdOrderByCreatedAtDesc(Long id, Pageable pageable);
}