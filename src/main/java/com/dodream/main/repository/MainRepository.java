package com.dodream.main.repository;

import com.dodream.book.entity.Book;
import com.dodream.study.entity.Study;
import com.dodream.study.enumtype.RoleEnum;
import java.util.List;
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

    @Query("SELECT s, " +
        "(SELECT COUNT(sm) FROM StudyMember sm "
        + "WHERE sm.study.id = s.id AND sm.role IN :roles) " +
        "AS memberCount " +
        "FROM Study s " +
        "WHERE s.title LIKE %:keyword%")
    Page<Study> findStudiesByTitle(@Param("keyword") String keyword,
        Pageable pageable, @Param("roles") List<String> roles);
}