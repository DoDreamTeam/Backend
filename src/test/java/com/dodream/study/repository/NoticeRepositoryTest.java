package com.dodream.study.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.dodream.common.enumtype.Category;
import com.dodream.study.entity.Notice;
import com.dodream.study.entity.Study;
import com.dodream.user.entity.User;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class NoticeRepositoryTest {

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private StudyRepository studyRepository;

    private Study study;

    @BeforeEach
    void setup() {
        study = Study.builder()
            .user(new User(1L))
            .description("TEST DESCRIPTION")
            .title("TEST STUDY")
            .category(Category.CATEGORY_CS)
            .build();

        study = studyRepository.save(study);
    }

    @DisplayName("스터디별 공지사항 조회")
    @Test
    public void getNotice() {
        // given (사전 준비)
        Notice notice = Notice.builder()
            .study(study)
            .content("Test Content")
            .isDeleted(false)
            .build();

        Notice savedNotice = noticeRepository.save(notice);

        // when (테스트 진행할 행위)
        Optional<Notice> foundNotice = noticeRepository.findByStudyIdAndNoticeId(study.getId(),
            savedNotice.getId());

        // then (행위에 대한 결과 검증)
        assertThat(foundNotice).isPresent();
        assertThat(foundNotice.get().getContent()).isEqualTo("Test Content");
        assertThat(foundNotice.get().isDeleted()).isFalse();
    }

    @DisplayName("스터디별 공지사항 조회가 안 되는 경우")
    @Test
    public void NotFoundGetNotice() {
        // when (테스트 진행할 행위) & then (행위에 대한 결과 검증)
        Optional<Notice> foundNotice = noticeRepository.findByStudyIdAndNoticeId(study.getId(), 10L);
        assertThat(foundNotice).isNotPresent();
    }

    @DisplayName("공지사항 삭제 후 검증")
    @Test
    public void testDeletedEmptyContentNoticeByStudyId() {
        // given (사전 준비)
        Notice deletedNotice = Notice.builder()
            .study(study)
            .content("")
            .isDeleted(true)
            .build();

        noticeRepository.save(deletedNotice);

        // when (테스트 진행할 행위)
        Optional<Notice> foundNotice = noticeRepository.findDeletedEmptyContentNoticeByStudyId(study.getId());

        // then (행위에 대한 결과 검증)
        assertThat(foundNotice).isPresent();
        assertThat(foundNotice.get().isDeleted()).isTrue();
        assertThat(foundNotice.get().getContent()).isEmpty();
    }

    @DisplayName("삭제된 공지사항을 찾지 못한 경우 - 공지사항이 존재하는 경우")
    @Test
    public void testFindDeletedEmptyContent() {
        // given (사전 준비)
        Notice savedNotice = Notice.builder()
            .study(study)
            .content("테스트 공지사항")
            .isDeleted(false)
            .build();

        noticeRepository.save(savedNotice);

        // when (테스트 진행할 행위)
        Optional<Notice> foundNotice = noticeRepository.findDeletedEmptyContentNoticeByStudyId(study.getId());

        // then (행위에 대한 결과 검증)
        assertThat(foundNotice).isNotPresent();
    }

    @DisplayName("삭제된 공지사항 조회")
    @Test
    public void testFindDeleted() {
        // given (사전 준비)
        Notice deletedNoticeWithContent = Notice.builder()
            .study(study)
            .content("비어있지 않은 내용")
            .isDeleted(true)
            .build();

        noticeRepository.save(deletedNoticeWithContent);

        // when (테스트 진행할 행위)
        Optional<Notice> foundNotice = noticeRepository.findDeletedEmptyContentNoticeByStudyId(study.getId());

        // then (행위에 대한 결과 검증)
        assertThat(foundNotice).isNotPresent();
    }


}