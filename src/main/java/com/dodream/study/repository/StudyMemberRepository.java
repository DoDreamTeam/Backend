package com.dodream.study.repository;

import com.dodream.study.entity.StudyMember;
import com.dodream.study.enumtype.RoleEnum;
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
public interface StudyMemberRepository extends JpaRepository<StudyMember, Long> {

//    @Query("select count(*) from StudyMember sm where sm.study.id = :id and sm.role != 'ROLE_WAITING'")
//    Long countAllByStudyId(@Param("id") Long id);
    @Query("SELECT sm.role FROM StudyMember sm WHERE sm.study.id = :studyId AND sm.user.id = :userId")
    Optional<RoleEnum> findRoleByStudyIdAndUserId(@Param("studyId") Long studyId, @Param("userId") Long userId);

    // 특정 사용자가 ROLE_MEMBER 또는 ROLE_LEADER인 스터디 정보를 페이징 처리하여 조회
    @Query("SELECT sm FROM StudyMember sm " +
        "JOIN FETCH sm.study s " +
        "WHERE sm.user = :user " +
        "AND sm.role IN :roles")
    Page<StudyMember> findByUserAndRoleIn(Pageable pageable, User user, List<RoleEnum> roles);
}
