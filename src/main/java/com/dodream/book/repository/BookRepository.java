package com.dodream.book.repository;

import com.dodream.book.entity.Book;
import com.dodream.common.enumtype.Category;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // 공개인 경우에만 문제집 전체 조회 가능
    List<Book> findAllBySecretFalse();

    // 카테고리별로 문제집 전체 조회 (공개인 경우)
    List<Book> findAllByCategoryAndSecretFalse(Category category);
}
