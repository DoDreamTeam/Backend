package com.dodream.study.repository;

import com.dodream.study.entity.QueCommentLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QueCommentLikeRepository extends JpaRepository<QueCommentLike, Long> {

    // 유저 ID 로 찿기
    Page<QueCommentLike> findByUserIdAndIsDeletedFalseOrderByQuecomment_CreatedAtDesc(Long userId,
        Pageable pageable);
}
