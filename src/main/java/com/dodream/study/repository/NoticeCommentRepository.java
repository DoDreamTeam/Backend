package com.dodream.study.repository;

import com.dodream.study.entity.NoticeComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeCommentRepository extends JpaRepository<NoticeComment, Long> {
    @Query("SELECT nc FROM NoticeComment nc " +
        "LEFT JOIN NoticeCommentLike ncl ON nc.id = ncl.noticeCommentId.id AND ncl.isDeleted = false " +
        "WHERE nc.notice.id = :noticeId " +
        "GROUP BY nc " +
        "ORDER BY COUNT(ncl.id) DESC") // 좋아요 수를 내림차순으로 정렬
    Page<NoticeComment> findByNoticeIdOrderByLikeCountDesc(Pageable pageable, @Param("noticeId") Long noticeId);

    Page<NoticeComment> findByNoticeIdOrderByCreatedAtDesc(Pageable pageable, Long id);
}
