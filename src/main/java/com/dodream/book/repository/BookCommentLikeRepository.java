package com.dodream.book.repository;

import com.dodream.book.entity.BookCommentLike;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookCommentLikeRepository extends JpaRepository<BookCommentLike , Long> {

    // 유저 ID 로 찿기
    List<BookCommentLike> findByUserId(Long userId);

}
