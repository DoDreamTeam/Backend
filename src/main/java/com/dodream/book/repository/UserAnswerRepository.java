package com.dodream.book.repository;

import com.dodream.book.entity.UserAnswer;
import com.dodream.book.enumtype.Evaluation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {
    
    // 사용자가 푼 문제 목록
    Page<UserAnswer> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    // 사용자가 푼 평가별 문제 목록
    Page<UserAnswer> findByUserIdAndEvaluationOrderByCreatedAtDesc(Long userId,
        Evaluation evaluation, Pageable pageable);
}
