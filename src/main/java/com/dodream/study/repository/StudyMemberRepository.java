package com.dodream.study.repository;

import com.dodream.study.entity.StudyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyMemberRepository extends JpaRepository<StudyMember, Long> {

    @Query("select count(*) from StudyMember sm where sm.study.id = :id and sm.role != 'ROLE_WAITING'")
    Long countAllByStudyId(@Param("id") Long id);
}
