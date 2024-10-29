package com.dodream.book.repository;

import com.dodream.book.entity.BookComment;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookCommentRepository extends JpaRepository<BookComment, Long> {

    // 최신순 댓글 전체 조회
    Page<BookComment> findByBookIdOrderByCreatedAtDesc(Pageable pageable, Long bookId);

    // 유저 댓글 목록 조회
    Page<BookComment> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // 좋아요순 댓글 전체 조회
    @Query("SELECT bc FROM BookComment bc LEFT JOIN BookCommentLike bcl ON bc.id = bcl.commentId.id AND bcl.isDeleted = false " +
        "WHERE bc.book.id = :bookId GROUP BY bc ORDER BY COUNT(bcl.id) DESC")
    Page<BookComment> findByBookIdOrderByLikeCountDesc(Pageable pageable, @Param("bookId") Long bookId);

    // 문제집 ID로 삭제
    void deleteByBookId(Long bookId);

}
