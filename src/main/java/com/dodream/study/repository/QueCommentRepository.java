package com.dodream.study.repository;

import com.dodream.study.entity.QueComment;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface QueCommentRepository extends JpaRepository<QueComment, Long> {
    @Query("SELECT qc FROM QueComment qc " +
        "LEFT JOIN QueCommentLike qcl ON qc.id = qcl.quecomment.id AND qcl.isDeleted = false " +
        "WHERE qc.studyAnswer.id = :studyAnswerId " +
        "GROUP BY qc " +
        "ORDER BY COUNT(qcl.id) DESC")
    Page<QueComment> findByStudyAnswerIdOrderByLikeCountDesc(Pageable pageable, @Param("studyAnswerId") Long studyAnswerId);

    // 특정 문제(StudyUserAnswer)에 대한 댓글을 생성일 순으로 가져오는 쿼리
    Page<QueComment> findByStudyAnswerIdOrderByCreatedAtDesc(Pageable pageable, Long id);

    // 유저 ID 로 찿기
    Page<QueComment> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
