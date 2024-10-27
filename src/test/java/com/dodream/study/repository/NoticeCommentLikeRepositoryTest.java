package com.dodream.study.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.dodream.study.entity.NoticeComment;
import com.dodream.study.entity.NoticeCommentLike;
import com.dodream.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class NoticeCommentLikeRepositoryTest {

    @Autowired
    private NoticeCommentLikeRepository noticeCommentLikeRepository;

    private User user;
    private NoticeComment noticeComment;
    private NoticeCommentLike noticeCommentLike;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        noticeComment = NoticeComment.builder()
            .id(1L)
            .content("Test Comment")
            .build();

        noticeCommentLike = NoticeCommentLike.builder()
            .noticeCommentId(noticeComment)
            .user(user)
            .isDeleted(false)
            .build();

        noticeCommentLikeRepository.save(noticeCommentLike);
    }

    @DisplayName("공지사항 댓글 좋아요 조회")
    @Test
    public void testGetNoticeCommentLike() {
        // given (사전 준비)
        // when (테스트 진행할 행위)
        long likeCount = noticeCommentLikeRepository.countByNoticeCommentIdAndIsDeletedFalse(noticeComment);

        // then (행위에 대한 결과 검증)
        assertEquals(1L, likeCount);
    }

    @DisplayName("공지사항 댓글 좋아요 조회 - 좋아요 취소 후 확인")
    @Test
    public void testGetNoticeCommentLikeIsDeleted() {
        // given (사전 준비)
        noticeCommentLike = NoticeCommentLike.builder()
            .noticeCommentId(noticeComment)
            .user(user)
            .isDeleted(true)
            .build();

        noticeCommentLikeRepository.save(noticeCommentLike);

        // when (테스트 진행할 행위)
        long likeCount = noticeCommentLikeRepository.countByNoticeCommentIdAndIsDeletedFalse(noticeComment);

        // then (행위에 대한 결과 검증)
        assertEquals(1L, likeCount);
    }

}