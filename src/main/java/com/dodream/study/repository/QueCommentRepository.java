package com.dodream.study.repository;

import com.dodream.study.entity.QueComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QueCommentRepository extends JpaRepository<QueComment, Long> {

    // 유저 ID 로 찿기
    Page<QueComment> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
