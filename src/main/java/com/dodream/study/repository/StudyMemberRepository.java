package com.dodream.study.repository;

import com.dodream.study.entity.StudyMember;
import com.dodream.study.enumtype.RoleEnum;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyMemberRepository extends JpaRepository<StudyMember, Long> {

//    @Query("select count(*) from StudyMember sm where sm.study.id = :id and sm.role != 'ROLE_WAITING'")
//    Long countAllByStudyId(@Param("id") Long id);
    @Query("SELECT sm.role FROM StudyMember sm WHERE sm.study.id = :studyId AND sm.user.id = :userId")
    Optional<RoleEnum> findRoleByStudyIdAndUserId(@Param("studyId") Long studyId, @Param("userId") Long userId);
}
