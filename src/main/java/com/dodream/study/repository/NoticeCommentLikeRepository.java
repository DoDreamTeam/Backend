package com.dodream.study.repository;

import com.dodream.study.entity.NoticeComment;
import com.dodream.study.entity.NoticeCommentLike;
import com.dodream.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeCommentLikeRepository extends JpaRepository<NoticeCommentLike, Long> {

    @Query("SELECT COUNT(nl) FROM NoticeCommentLike nl WHERE nl.noticeCommentId = :noticeCommentId AND nl.isDeleted = false")
    long countByNoticeCommentIdAndIsDeletedFalse(@Param("noticeCommentId") NoticeComment noticeCommentId);

    Optional<NoticeCommentLike> findByUserAndNoticeCommentId(User user, NoticeComment noticeComment);
}
