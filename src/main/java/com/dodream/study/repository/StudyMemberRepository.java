package com.dodream.study.repository;

import com.dodream.study.domain.StudyResponse;
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
    @Query("SELECT sm FROM StudyMember sm JOIN FETCH sm.user WHERE sm.study.id = :studyId AND sm.role = :role")
    Page<StudyMember> findByStudyMemberId(Long studyId, RoleEnum role, Pageable pageable);
    @Query("SELECT sm.role FROM StudyMember sm WHERE sm.study.id = :studyId AND sm.user.id = :userId")
    Optional<RoleEnum> findRoleByStudyIdAndUserId(@Param("studyId") Long studyId, @Param("userId") Long userId);

    @Query("SELECT new com.dodream.study.domain.StudyResponse(s.id, s.title, s.user.username, "
        + "s.user.profileImage, s.description, s.category, " +
        "(SELECT COUNT(sm2) FROM StudyMember sm2 WHERE sm2.study.id = s.id), s.updatedAt, s.createdAt) " +
        "FROM StudyMember sm " +
        "JOIN sm.study s " +
        "WHERE sm.user = :user " +
        "AND sm.role IN :roles")
    Page<StudyResponse> findByUserAndRoleIn(Pageable pageable, @Param("user") User user, @Param("roles") List<RoleEnum> roles);

    Optional<StudyMember> findByUserIdAndStudyId(Long userId, Long studyId);

    @Query("SELECT sm FROM StudyMember sm WHERE sm.study.id = :studyId AND sm.role = 'ROLE_LEADER'")
    Optional<StudyMember> findLeaderByStudyId(@Param("studyId") Long studyId);

}
