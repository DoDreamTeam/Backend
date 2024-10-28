package com.dodream.book.repository;

import com.dodream.book.entity.Question;
import com.dodream.book.entity.UserAnswer;
import com.dodream.book.enumtype.Evaluation;
import com.dodream.user.entity.User;
import java.util.Optional;
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

    // 기존에 사용자가 문제를 풀었었는지 체크
    UserAnswer findByUserIdAndQuestionId(Long id, Long questionId);

    // 사용자가 특정 질문에 대해 제출한 답변 조회
    Optional<UserAnswer> findByUserAndQuestion(User user, Question question);

}
