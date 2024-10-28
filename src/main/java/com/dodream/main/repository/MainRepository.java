package com.dodream.main.repository;

import com.dodream.book.entity.Book;
import com.dodream.study.entity.Study;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MainRepository extends JpaRepository<Book, Long> {
    @Query("SELECT b FROM Book b WHERE b.title LIKE %:keyword% AND b.secret = false")
    Page<Book> findBooksByTitle(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT s FROM Study s WHERE s.title LIKE %:keyword%")
    Page<Study> findStudiesByTitle(@Param("keyword") String keyword, Pageable pageable);
}