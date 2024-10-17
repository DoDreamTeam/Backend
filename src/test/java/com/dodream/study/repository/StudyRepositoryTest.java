package com.dodream.study.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.dodream.common.enumtype.Category;
import com.dodream.study.entity.Study;
import com.dodream.study.enumtype.RoleEnum;
import com.dodream.user.entity.User;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class StudyRepositoryTest {

    @Autowired
    private StudyRepository studyRepository;

    @DisplayName("스터디 등록")
    @Test
    public void savedStudy() {
        // given (사전 준비)
        Study study = Study.builder()
            .title("TEST Study")
            .category(Category.CATEGORY_CS)
            .description("CS 스터디 입니다.")
            .user(new User(1L))
            .build();

        // when (테스트 진행할 행위)
        Study savedStudy = studyRepository.save(study);

        // then (행위에 대한 결과 검증)
        assertThat(savedStudy).isNotNull();
        assertThat(savedStudy.getId()).isNotNull();
        assertThat(savedStudy.getTitle()).isEqualTo("TEST Study");
        assertThat(savedStudy.getDescription()).isEqualTo("CS 스터디 입니다.");
        assertThat(savedStudy.getCategory()).isEqualByComparingTo(Category.CATEGORY_CS);
    }

    @DisplayName("스터디 전체 조회")
    @Test
    public void findAllStudy() {
        // given (사전 준비)
        Study study1 = Study.builder()
            .title("TEST Study 1")
            .category(Category.CATEGORY_CERT)
            .description("자격증 스터디 입니다.")
            .user(new User(1L))
            .build();
        studyRepository.save(study1);

        Study study2 = Study.builder()
            .title("TEST Study 2")
            .category(Category.CATEGORY_ETC)
            .description("삼성전자 면접 스터디 입니다.")
            .user(new User(2L))
            .build();
        studyRepository.save(study2);

        // when (테스트 진행할 행위)
        List<Study> studyList = studyRepository.findAll();

        // then (행위에 대한 결과 검증)
        assertThat(studyList).isNotNull();
        assertThat(studyList.size()).isGreaterThanOrEqualTo(2);
        assertThat(
            studyList.stream().anyMatch(study -> study.getTitle().equals("TEST Study 1"))).isTrue();
        assertThat(studyList.stream()
            .anyMatch(study -> study.getDescription().equals("삼성전자 면접 스터디 입니다."))).isTrue();
    }

    @DisplayName("특정 스터디(CATEGORY) 조회")
    @Test
    public void findByCategoryTest() {
        // given (사전 준비)
        Study study = Study.builder()
            .title("TEST Study 1")
            .category(Category.CATEGORY_CERT)
            .description("자격증 스터디 입니다.")
            .user(new User(1L))
            .build();

        Study savedStudy = studyRepository.save(study);

        // when (테스트 진행할 행위)
        Study foundStudy = studyRepository.findByCategory(savedStudy.getCategory());

        // then (행위에 대한 결과 검증)
        assertThat(foundStudy).isNotNull();
        assertThat(foundStudy.getId()).isEqualTo(savedStudy.getId());
        assertThat(foundStudy.getTitle()).isEqualTo(savedStudy.getTitle());
        assertThat(foundStudy.getDescription()).isEqualTo(savedStudy.getDescription());
    }

    @DisplayName("특정 스터디 삭제")
    @Test
    public void deleteStudyById() {
        // given (사전 준비)
        int originSize = studyRepository.findAll().size();
        Study study = Study.builder()
            .title("TEST Study 1")
            .category(Category.CATEGORY_CERT)
            .description("자격증 스터디 입니다.")
            .user(new User(1L))
            .build();

        Study savedStudy = studyRepository.save(study);

        // when (테스트 진행할 행위)
        try {
            if (Objects.equals(RoleEnum.ROLE_LEADER.getRole(), "ROLE_LEADER")) {
                studyRepository.deleteById(savedStudy.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int newSize = studyRepository.findAll().size();

        // then (행위에 대한 결과 검증)
        assertThat(originSize).isEqualTo(newSize);
    }

    @DisplayName("제목 또는 내용 검색")
    @Test
    public void searchByTitleOrDescription() {
        // given (사전 준비)
        Study study1 = Study.builder()
            .title("삼전 면접 스터디")
            .category(Category.CATEGORY_ETC)
            .description("삼전 check it out")
            .user(new User(1L))
            .build();

        Study savedStudy1 = studyRepository.save(study1);

        Study study2 = Study.builder()
            .title("삼전 온라인 면접 스터디")
            .category(Category.CATEGORY_ETC)
            .description("삼전 면접 빡세게 준비.")
            .user(new User(2L))
            .build();

        Study savedStudy2 = studyRepository.save(study2);
        String keyword = "면접";

        // when (테스트 진행할 행위)
        List<Study> studyList = studyRepository
            .findByTitleContainsOrDescriptionContainsOrderByTitleAsc(
                keyword, keyword);

        // then (행위에 대한 결과 검증)
        assertThat(studyList.indexOf(savedStudy2)).isGreaterThan(studyList.indexOf(savedStudy1));
        assertThat(studyList.stream().allMatch(
            study -> study.getTitle().contains(keyword) || study.getDescription()
                .contains(keyword))).isTrue();
    }

}