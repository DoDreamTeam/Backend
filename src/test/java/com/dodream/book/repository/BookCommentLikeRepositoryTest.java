package com.dodream.book.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.dodream.book.entity.Book;
import com.dodream.book.entity.BookComment;
import com.dodream.book.entity.BookCommentLike;
import com.dodream.common.enumtype.Category;
import com.dodream.user.entity.User;
import com.dodream.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class BookCommentLikeRepositoryTest {

    @Autowired
    private BookCommentLikeRepository bookCommentLikeRepository;

    @Autowired
    private BookCommentRepository bookCommentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    private User user;
    private Book book;
    private BookComment comment;

    @BeforeEach
    public void setUp() {
        // 테스트에 필요한 User와 Book을 생성 및 저장
        user = User.builder()
            .username("testUser")
            .provider("provider1")
            .providerId("1")
            .build();
        userRepository.save(user);

        book = Book.builder()
            .title("Test Book")
            .category(Category.CATEGORY_ETC)
            .secret(false)
            .user(user)
            .build();
        bookRepository.save(book);

        comment = BookComment.builder()
            .comment("comment 1")
            .user(user)
            .book(book)
            .createdAt(LocalDateTime.now().minusDays(1))
            .build();
        bookCommentRepository.save(comment);
    }

    @DisplayName("좋아요 수 카운트 테스트")
    @Test
    public void countByCommentIdAndIsDeletedFalseTest() {
        // given
        BookCommentLike like1 = BookCommentLike.builder()
            .user(user)
            .commentId(comment)
            .isDeleted(false)
            .build();
        BookCommentLike like2 = BookCommentLike.builder()
            .user(user)
            .commentId(comment)
            .isDeleted(false)
            .build();
        bookCommentLikeRepository.save(like1);
        bookCommentLikeRepository.save(like2);

        // when
        long count = bookCommentLikeRepository.countByCommentIdAndIsDeletedFalse(comment);

        // then
        assertEquals(2, count);
    }

    @DisplayName("좋아요 유저 ID로 찾기")
    @Test
    public void findByUserIdAndIsDeletedFalseOrderByCommentId_CreatedAtDescTest() {
        // given
        BookCommentLike like = BookCommentLike.builder()
            .user(user)
            .commentId(comment)
            .isDeleted(false)
            .build();
        bookCommentLikeRepository.save(like);

        // when
        List<BookCommentLike> likes = bookCommentLikeRepository.findByUserIdAndIsDeletedFalseOrderByCommentId_CreatedAtDesc(
            user.getId(), PageRequest.of(0, 10)).getContent();

        // then
        assertFalse(likes.isEmpty());
        assertEquals(1, likes.size());
        assertEquals(like.getId(), likes.get(0).getId());
    }

    @DisplayName("좋아요 조회 테스트")
    @Test
    public void findByUserAndCommentIdTest() {
        // given
        BookCommentLike like = BookCommentLike.builder()
            .user(user)
            .commentId(comment)
            .isDeleted(false)
            .build();
        bookCommentLikeRepository.save(like);

        // when
        Optional<BookCommentLike> foundLike = bookCommentLikeRepository.findByUserAndCommentId(user, comment);

        // then
        assertTrue(foundLike.isPresent());
        assertEquals(like.getId(), foundLike.get().getId());
    }

    @DisplayName("취소된 좋아요는 카운트하지 않는다")
    @Test
    public void countByCommentIdAndIsDeletedFalseShouldNotCountDeletedTest() {
        // given
        BookCommentLike like1 = BookCommentLike.builder()
            .user(user)
            .commentId(comment)
            .isDeleted(false)
            .build();
        BookCommentLike like2 = BookCommentLike.builder()
            .user(user)
            .commentId(comment)
            .isDeleted(true)
            .build();
        bookCommentLikeRepository.save(like1);
        bookCommentLikeRepository.save(like2);

        // when
        long count = bookCommentLikeRepository.countByCommentIdAndIsDeletedFalse(comment);

        // then
        assertEquals(1, count);
    }

    @DisplayName("좋아요 삭제 테스트")
    @Test
    public void deleteByCommentIdTest() {
        // given
        BookCommentLike like = BookCommentLike.builder()
            .user(user)
            .commentId(comment)
            .isDeleted(false)
            .build();
        bookCommentLikeRepository.save(like);

        // when
        bookCommentLikeRepository.deleteByCommentId(comment);

        // then
        long count = bookCommentLikeRepository.countByCommentIdAndIsDeletedFalse(comment);
        assertEquals(0, count); // 좋아요가 삭제되어야 하므로 카운트는 0이어야 함
    }
}
