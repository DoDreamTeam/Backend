package com.dodream.book.repository;

import com.dodream.book.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    // 최신순으로 문제 전체 조회 (문제, 생성 날짜만 보여짐)
    Page<Question> findByBookIdOrderByCreatedAtDesc(Pageable pageable, Long bookId);

    // 제목으로 검색하는 메서드
    Page<Question> findByBookIdAndQuestionContainingOrderByCreatedAtDesc(Long bookId, String title, Pageable pageable);

}
