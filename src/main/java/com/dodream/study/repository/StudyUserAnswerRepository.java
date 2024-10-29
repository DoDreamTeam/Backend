package com.dodream.study.repository;

import com.dodream.study.entity.StudyUserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyUserAnswerRepository extends JpaRepository<StudyUserAnswer, Long> {

    // user answer ID로 삭제
    void deleteByUserAnswerId(Long questionId);

}
