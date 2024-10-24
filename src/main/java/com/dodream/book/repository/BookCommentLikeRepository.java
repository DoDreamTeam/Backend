package com.dodream.book.repository;

import com.dodream.book.entity.BookComment;
import com.dodream.book.entity.BookCommentLike;
import com.dodream.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookCommentLikeRepository extends JpaRepository<BookCommentLike, Long> {

    // 좋아요 수 계산 (isDeleted가 false인 것들만)
    @Query("SELECT COUNT(bl) FROM BookCommentLike bl WHERE bl.commentId = :commentId AND bl.isDeleted = false")
    long countByCommentIdAndIsDeletedFalse(@Param("commentId") BookComment commentId);

    // 유저 ID로 찾기
    List<BookCommentLike> findByUserId(Long userId);

    // 좋아요 조회
    Optional<BookCommentLike> findByUserAndCommentId(User user, BookComment commentId);
}
