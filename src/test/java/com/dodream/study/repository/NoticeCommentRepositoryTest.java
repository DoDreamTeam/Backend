package com.dodream.study.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.dodream.common.enumtype.Category;
import com.dodream.study.entity.Notice;
import com.dodream.study.entity.NoticeComment;
import com.dodream.study.entity.NoticeCommentLike;
import com.dodream.study.entity.Study;
import com.dodream.user.entity.User;
import com.dodream.user.repository.UserRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class NoticeCommentRepositoryTest {

    @Autowired
    private NoticeCommentRepository noticeCommentRepository;

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private NoticeCommentLikeRepository noticeCommentLikeRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Notice testNotice;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .username("testUser")
            .provider("provider1")
            .providerId("1")
            .build();
        userRepository.save(testUser);

        Study testStudy = Study.builder()
            .id(1L)
            .title("Test Study")
            .user(testUser)
            .category(Category.CATEGORY_ETC)
            .description("Study Description")
            .build();

        testNotice = Notice.builder()
            .content("Test Notice")
            .study(testStudy)
            .isDeleted(false)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        noticeRepository.save(testNotice);
    }

    @DisplayName("공지사항 댓글 추가")
    @Test
    public void testAddNoticeComment() {
        // given (사전 준비)
        NoticeComment comment = NoticeComment.builder()
            .content("Test Comment")
            .user(testUser)
            .notice(testNotice)
            .build();

        NoticeComment savedComment = noticeCommentRepository.save(comment);

        // when (테스트 진행할 행위)
        // then (행위에 대한 결과 검증)
        assertThat(savedComment).isNotNull();
        assertEquals("Test Comment", savedComment.getContent());
        assertEquals(testUser, savedComment.getUser());
        assertEquals(testNotice, savedComment.getNotice());
    }

    @DisplayName("좋아요 수 기준으로 공지사항 댓글 조회")
    @Test
    public void testGetNoticeCommentOrderByLikeCountDesc() {
        // given (사전 준비)
        NoticeComment comment1 = NoticeComment.builder()
            .content("Comment 1")
            .user(testUser)
            .notice(testNotice)
            .build();

        NoticeComment comment2 = NoticeComment.builder()
            .content("Comment 2")
            .user(testUser)
            .notice(testNotice)
            .build();

        comment1 = noticeCommentRepository.save(comment1);
        comment2 = noticeCommentRepository.save(comment2);

        noticeCommentLikeRepository.save(NoticeCommentLike.builder()
                .noticeCommentId(comment1)
                .user(testUser)
                .isDeleted(false)
                .build());

        noticeCommentLikeRepository.save(NoticeCommentLike.builder()
                .noticeCommentId(comment2)
                .user(testUser)
                .isDeleted(false)
                .build());

        // when (테스트 진행할 행위)
        Page<NoticeComment> result = noticeCommentRepository.findByNoticeIdOrderByLikeCountDesc(
            PageRequest.of(0, 10), testNotice.getId());

        // then (행위에 대한 결과 검증)
        assertThat(result.getContent().size()).isGreaterThanOrEqualTo(2);
        assertEquals(comment1.getId(), result.getContent().get(0).getId());
    }

    @DisplayName("생성일자 기준 공지사항 조회")
    @Test
    public void testGetNoticeCommentOrderByCreatedAtDesc() {
        // given (사전 준비)
        NoticeComment comment1 = NoticeComment.builder()
            .content("이전 댓글")
            .user(testUser)
            .notice(testNotice)
            .build();

        NoticeComment comment2 = NoticeComment.builder()
            .content("새로운 댓글")
            .user(testUser)
            .notice(testNotice)
            .build();

        noticeCommentRepository.save(comment1);
        noticeCommentRepository.save(comment2);

        // when (테스트 진행할 행위)
        Page<NoticeComment> result = noticeCommentRepository.findByNoticeIdOrderByCreatedAtDesc(
            PageRequest.of(0, 10), testNotice.getId());

        // then (행위에 대한 결과 검증)
        assertEquals(comment2.getId(), result.getContent().get(0).getId());
        assertEquals(comment1.getId(), result.getContent().get(1).getId());
    }


}