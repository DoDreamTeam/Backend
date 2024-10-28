package com.dodream.study.repository;

import com.dodream.study.entity.QueComment;
import com.dodream.study.entity.QueCommentLike;
import com.dodream.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QueCommentLikeRepository extends JpaRepository<QueCommentLike, Long> {

    // 특정 댓글에 좋아요 수를 조회
    @Query("SELECT COUNT(ql) FROM QueCommentLike ql WHERE ql.quecomment = :queCommentId AND ql.isDeleted = false")
    long countByQueCommentIdAndIsDeletedFalse(@Param("queCommentId") QueComment queCommentId);

    // 특정 사용자가 특정 댓글에 좋아요를 눌렀는지 확인하는 메서드
    Optional<QueCommentLike> findByUserAndQuecomment(User user, QueComment queComment);

    // 유저 ID 로 찿기
    Page<QueCommentLike> findByUserIdAndIsDeletedFalseOrderByQuecomment_CreatedAtDesc(Long userId,
        Pageable pageable);
}
