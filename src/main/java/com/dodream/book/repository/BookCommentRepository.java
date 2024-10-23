package com.dodream.book.repository;

import com.dodream.book.entity.BookComment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookCommentRepository extends JpaRepository<BookComment, Long> {

    // 유저 ID 로 찿기
    List<BookComment> findByUserId(Long userId);
}
