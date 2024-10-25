package com.dodream.book.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.dodream.book.entity.Book;
import com.dodream.book.entity.BookComment;
import com.dodream.common.enumtype.Category;
import com.dodream.user.entity.User;
import com.dodream.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
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
class BookCommentRepositoryTest {

    @Autowired
    private BookCommentRepository bookCommentRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;


    @DisplayName("문제집 ID로 최신순 댓글 조회")
    @Test
    public void findByBookIdOrderByCreatedAtDescTest() {
        // Given
        User user = User.builder()
            .username("testUser")
            .provider("provider1")
            .providerId("1")
            .build();
        userRepository.save(user);

        Book book = Book.builder()
            .title("Test Book")
            .user(user)
            .category(Category.CATEGORY_CS)
            .secret(false)
            .createdAt(LocalDateTime.now())
            .build();
        bookRepository.save(book);

        BookComment comment1 = BookComment.builder()
            .comment("First comment")
            .user(user)
            .book(book)
            .createdAt(LocalDateTime.now().minusDays(1)) // 1일 전
            .build();
        BookComment comment2 = BookComment.builder()
            .comment("Second comment")
            .user(user)
            .book(book)
            .createdAt(LocalDateTime.now()) // 현재 시간
            .build();
        bookCommentRepository.save(comment2); // 최신 댓글 먼저 저장
        bookCommentRepository.save(comment1); // 그 다음에 오래된 댓글 저장

        // When
        Page<BookComment> comments = bookCommentRepository.findByBookIdOrderByCreatedAtDesc(
            PageRequest.of(0, 5), book.getId());

        // Then
        assertThat(comments.getContent()).hasSize(2);
        assertThat(comments.getContent().get(0).getComment()).isEqualTo("Second comment");
        assertThat(comments.getContent().get(1).getComment()).isEqualTo("First comment");
    }

    @DisplayName("유저 ID로 댓글 조회")
    @Test
    public void findByUserIdTest() {
        // Given
        User user = User.builder()
            .username("testUser")
            .provider("provider1")
            .providerId("1")
            .build();
        userRepository.save(user);

        Book book = Book.builder()
            .title("Test Book")
            .user(user)
            .category(Category.CATEGORY_CS)
            .secret(false)
            .createdAt(LocalDateTime.now())
            .build();
        bookRepository.save(book);

        BookComment comment1 = BookComment.builder()
            .comment("Comment 1")
            .user(user)
            .book(book)
            .createdAt(LocalDateTime.now())
            .build();
        BookComment comment2 = BookComment.builder()
            .comment("Comment 2")
            .user(user)
            .book(book)
            .createdAt(LocalDateTime.now())
            .build();
        bookCommentRepository.save(comment1);
        bookCommentRepository.save(comment2);

        // When
        Page<BookComment> commentsPage = bookCommentRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), PageRequest.of(0, 10));

        // Then
        assertThat(commentsPage.getContent()).hasSize(2);
        assertThat(commentsPage.getContent()).extracting("comment").containsExactlyInAnyOrder("Comment 1", "Comment 2");
    }


    @DisplayName("좋아요순 댓글 전체 조회")
    @Test
    public void findByBookIdOrderByLikeCountDescTest() {
        // Given
        User user = User.builder()
            .username("testUser")
            .provider("provider1")
            .providerId("1")
            .build();
        userRepository.save(user);

        Book book = Book.builder()
            .title("Test Book")
            .user(user)
            .category(Category.CATEGORY_CS)
            .secret(false)
            .createdAt(LocalDateTime.now())
            .build();
        bookRepository.save(book);


        BookComment comment1 = BookComment.builder()
            .comment("Comment with likes")
            .user(user)
            .book(book)
            .createdAt(LocalDateTime.now())
            .build();
        BookComment comment2 = BookComment.builder()
            .comment("Another comment")
            .user(user)
            .book(book)
            .createdAt(LocalDateTime.now())
            .build();
        bookCommentRepository.save(comment1);
        bookCommentRepository.save(comment2);

        // When
        Page<BookComment> comments = bookCommentRepository.findByBookIdOrderByLikeCountDesc(PageRequest.of(0, 10), book.getId());

        // Then
        assertThat(comments).isNotEmpty();
    }

}