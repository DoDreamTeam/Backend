package com.dodream.study.repository;

import com.dodream.common.enumtype.Category;
import com.dodream.study.entity.Study;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyRepository extends JpaRepository<Study, Long> {
    Study findByCategory(Category category);

    List<Study> findByTitleContainsOrDescriptionContainsOrderByTitleAsc(
        String titleKeyword, String descriptionKeyword);
}
