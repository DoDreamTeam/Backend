package com.dodream.book.repository;

import com.dodream.book.entity.Question;
import com.dodream.book.entity.UserBook;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBookRepository extends JpaRepository<UserBook, Long> {

    // 문제 ID로 삭제
    void deleteByQuestionId(Question question);
}
