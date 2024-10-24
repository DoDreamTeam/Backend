package com.dodream.study.repository;

import com.dodream.common.enumtype.Category;
import com.dodream.study.domain.StudyResponse;
import com.dodream.study.entity.Study;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyRepository extends JpaRepository<Study, Long> {
    Study findByCategory(Category category);

    List<Study> findByTitleContainsOrDescriptionContainsOrderByTitleAsc(
        String titleKeyword, String descriptionKeyword);

    /* 스터디 카테고리별로 검색 */
    @Query("SELECT new com.dodream.study.domain.StudyResponse(s.id, s.title, s.user.username, "
        + "s.description, s.category, null, s.updatedAt, s.createdAt, "
        + "(SELECT COUNT(sm) FROM StudyMember sm WHERE sm.study.id = s.id)) "
        + "FROM Study s "
        + "WHERE (:category IS NULL OR s.category = :category) ")
    Page<StudyResponse> findByStudyCategory(Pageable pageable, @Param("category") Category category);

    /* 스터디 키워드(제목 + 내용 or 사용자) 로 검색 */
    @Query("SELECT new com.dodream.study.domain.StudyResponse(s.id, s.title, s.user.username, "
        + "s.description, s.category, null, s.updatedAt, s.createdAt,"
        + "(SELECT COUNT(sm) FROM StudyMember sm WHERE sm.study.id = s.id)) "
        + "FROM Study s "
        + "WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')) "
        + "OR LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%')) "
        + "OR LOWER(s.user.username) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<StudyResponse> findStudiesByTitleDescriptionOrUsername(Pageable pageable, @Param("keyword") String keyword);

    /* 스터디 전체 조회 */
    @Query("SELECT new com.dodream.study.domain.StudyResponse(s.id, s.title, s.user.username, "
        + "s.description, s.category, null, s.updatedAt, s.createdAt, "
        + "(SELECT COUNT(sm) FROM StudyMember sm WHERE sm.study.id = s.id)) FROM Study s")
    Page<StudyResponse> findAllStudy(Pageable pageable);

    /* 인기 스터디 조회 */
    @Query("SELECT new com.dodream.study.domain.StudyResponse(s.id, s.title, s.user.username, "
        + "s.description, s.category, null, s.updatedAt, s.createdAt as update, "
        + "(SELECT COUNT(sm) FROM StudyMember sm WHERE sm.study.id = s.id) as count ) FROM Study s "
        + "ORDER BY count DESC, update DESC ")
    Page<StudyResponse> findAllStudyWithMemberCount(Pageable pageable);
}
