package com.dodream.book.repository;

import com.dodream.book.entity.Question;
import com.dodream.book.entity.UserAnswer;
import com.dodream.book.enumtype.Evaluation;
import com.dodream.study.domain.StudyUserQueAnswerResponse;
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
public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {
    
    // 사용자가 푼 문제 목록
    Page<UserAnswer> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // 사용자가 푼 문제 목록
    List<UserAnswer> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 사용자가 푼 평가별 문제 목록
    Page<UserAnswer> findByUserIdAndEvaluationOrderByCreatedAtDesc(Long userId,
        Evaluation evaluation, Pageable pageable);

    // 기존에 사용자가 문제를 풀었었는지 체크
    UserAnswer findByUserIdAndQuestionId(Long id, Long questionId);

    // 사용자가 특정 질문에 대해 제출한 답변 조회
    Optional<UserAnswer> findByUserAndQuestion(User user, Question question);

    // 사용자가 푼 문제의 평가 목록 조회 (질문 ID 목록을 기반으로)
    List<UserAnswer> findByUserIdAndQuestionIdIn(Long userId, List<Long> questionIds);

    // 문제 ID로 삭제
    void deleteByQuestionId(Long questionId);

    // 스터디방에 추가된 문제 조회
    @Query("SELECT new com.dodream.study.domain.StudyUserQueAnswerResponse("
        + "q.id, q.question, q.modelAnswer, ua.user.username, ua.user.profileImage, ua.evaluation, ua.createdAt) " +
        "FROM StudyUserAnswer sua " +
        "JOIN sua.userAnswer ua " +
        "JOIN ua.question q " +
        "LEFT JOIN ua.user u " +
        "WHERE sua.study.id = :studyId OR ua.user = :user")
    Page<StudyUserQueAnswerResponse> findStudyUserAnswers(Pageable pageable,
        @Param("studyId") Long studyId,
        @Param("user") User user);

    // 스터디방에서 내가 푼 문제 조회
    @Query("SELECT new com.dodream.study.domain.StudyUserQueAnswerResponse("
        + "q.id, q.question, q.modelAnswer, ua.user.username, ua.user.profileImage, ua.evaluation, ua.createdAt) " +
        "FROM StudyUserAnswer sua " +
        "JOIN sua.userAnswer ua " +
        "JOIN ua.question q " +
        "LEFT JOIN ua.user u " +
        "WHERE sua.study.id = :studyId AND ua.user = :user")
    Page<StudyUserQueAnswerResponse> findStudyMyUserAnswers(Pageable pageable,
        @Param("studyId") Long studyId,
        @Param("user") User user);

    // 스터디방에서 내가 풀지 않은 문제 조회
    @Query("SELECT new com.dodream.study.domain.StudyUserQueAnswerResponse("
        + "q.id, q.question, q.modelAnswer, ua.user.username, ua.user.profileImage, ua.evaluation, ua.createdAt) " +
        "FROM StudyUserAnswer sua " +
        "JOIN sua.userAnswer ua " +
        "JOIN ua.question q " +
        "LEFT JOIN ua.user u " +
        "WHERE sua.study.id = :studyId AND ua.user != :user")
    Page<StudyUserQueAnswerResponse> findStudyOtherUserAnswers(Pageable pageable,
        @Param("studyId") Long studyId,
        @Param("user") User user);

    @Query("SELECT new com.dodream.study.domain.StudyUserQueAnswerResponse("
        + "q.id, q.question, q.modelAnswer, ua.user.username, "
        + "ua.user.profileImage, ua.evaluation, ua.createdAt) " +
        "FROM StudyUserAnswer sua " +
        "JOIN sua.userAnswer ua " +
        "JOIN ua.question q " +
        "WHERE sua.study.id = :studyId " +
        "AND (q.question LIKE %:keyword% OR q.modelAnswer LIKE %:keyword%) " +
        "AND ua.user = :user")
    Page<StudyUserQueAnswerResponse> searchStudyUserAnswers(Pageable pageable,
        @Param("studyId") Long studyId,
        @Param("user") User user,
        @Param("keyword") String keyword);

}
