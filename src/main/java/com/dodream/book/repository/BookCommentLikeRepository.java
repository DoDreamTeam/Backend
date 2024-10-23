package com.dodream.book.repository;

import com.dodream.book.entity.BookComment;
import com.dodream.book.entity.BookCommentLike;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookCommentLikeRepository extends JpaRepository<BookCommentLike, Long> {

    // 좋아요 수 계산
    long countByCommentId(BookComment commentId);
  
    // 유저 ID 로 찿기
    List<BookCommentLike> findByUserId(Long userId);

}
