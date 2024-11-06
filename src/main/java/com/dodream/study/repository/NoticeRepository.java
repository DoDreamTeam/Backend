package com.dodream.study.repository;

import com.dodream.study.entity.Notice;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    @Query("SELECT n FROM Notice n WHERE n.study.id = :studyId AND n.id = :noticeId AND n.isDeleted = false")
    Optional<Notice> findByStudyIdAndNoticeId(@Param("studyId") Long studyId, @Param("noticeId") Long noticeId);

    @Query("SELECT n FROM Notice n WHERE n.study.id = :studyId AND n.isDeleted = true AND n.content = ''")
    Optional<Notice> findDeletedEmptyContentNoticeByStudyId(@Param("studyId") Long studyId);

    Optional<Notice> findByStudyId(Long studyId);

}
