package com.dodream.book.repository;

import com.dodream.book.entity.BookComment;
import com.dodream.book.entity.BookCommentLike;
import com.dodream.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookCommentLikeRepository extends JpaRepository<BookCommentLike, Long> {

    // 좋아요 수 계산 (isDeleted가 false인 것들만)
    @Query("SELECT COUNT(bl) FROM BookCommentLike bl WHERE bl.commentId = :commentId AND bl.isDeleted = false")
    long countByCommentIdAndIsDeletedFalse(@Param("commentId") BookComment commentId);

    // 유저 좋아요 목록 조회
    Page<BookCommentLike> findByUserIdAndIsDeletedFalseOrderByCommentId_CreatedAtDesc(Long userId,
        Pageable pageable);

    // 좋아요 조회
    Optional<BookCommentLike> findByUserAndCommentId(User user, BookComment commentId);

    // comment ID로 삭제
    void deleteByCommentId(BookComment commentId);

    // 로그인한 사용자가 좋아요 했는지 여부
    boolean existsByUserIdAndCommentId(Long user_id, BookComment commentId);

}
