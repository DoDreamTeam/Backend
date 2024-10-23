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

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class BookCommentRepositoryTest {

    @Autowired
    private BookCommentRepository bookCommentRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("문제집 전체 댓글 최신순 조회")
    @Test
    public void getAllCommentsTest() {
        // given (사전 준비)
        User user1 = User
            .builder()
            .username("hello")
            .provider("provider1")
            .providerId("1")
            .build();

        User user2 = User
            .builder()
            .username("hello2")
            .provider("provider1")
            .providerId("2")
            .build();

        userRepository.save(user1);
        userRepository.save(user2);

        Book book = Book
            .builder()
            .id(1L)
            .title("Test Book1")
            .user(user1)
            .category(Category.CATEGORY_CS)
            .secret(false)
            .createdAt(LocalDateTime.now().minusDays(1))
            .build();

        bookRepository.save(book);


        BookComment comment1 = BookComment
            .builder()
            .comment("comment 1")
            .user(user1)
            .book(book)
            .createdAt(LocalDateTime.now().minusDays(1))
            .build();

        BookComment comment2 = BookComment
            .builder()
            .comment("comment 2")
            .user(user2)
            .book(book)
            .createdAt(LocalDateTime.now())
            .build();

        bookCommentRepository.save(comment1);
        bookCommentRepository.save(comment2);

        // when (테스트 진행할 범위)
        List<BookComment> comments = bookCommentRepository.findByBookIdOrderByCreatedAtDesc(book.getId());


        // then (범위에 대한 결과 검증)
        assertEquals(2, comments.size());
        assertEquals("comment 2", comments.get(0).getComment());
        assertEquals("comment 1", comments.get(1).getComment());

    }

    @DisplayName("문제집 댓글 생성")
    @Test
    public void addCommentTest() {
        // given (사전 준비)
        User user1 = User
            .builder()
            .username("hello")
            .provider("provider1")
            .providerId("1")
            .build();

        userRepository.save(user1);

        Book book = Book
            .builder()
            .id(1L)
            .title("Test Book1")
            .user(user1)
            .category(Category.CATEGORY_CS)
            .secret(false)
            .createdAt(LocalDateTime.now().minusDays(1))
            .build();

        bookRepository.save(book);

        BookComment comment1 = BookComment
            .builder()
            .comment("comment 1")
            .user(user1)
            .book(book)
            .createdAt(LocalDateTime.now().minusDays(1))
            .build();

        // when (테스트 진행할 범위)
        BookComment savedComment = bookCommentRepository.save(comment1);

        // then (범위에 대한 결과 검증)
        assertThat(savedComment).isNotNull(); // null 이 아닌가?
        assertThat(savedComment.getId()).isNotNull();
        assertThat(savedComment.getComment()).isEqualTo("comment 1");
        assertThat(savedComment.getUser().getUsername()).isEqualTo("hello");
    }

    @Test
    @DisplayName("문제집 댓글 수정")
    public void testUpdateBookComment() {
        // given (사전 준비)
        User user1 = User
            .builder()
            .username("hello")
            .provider("provider1")
            .providerId("1")
            .build();

        userRepository.save(user1);

        Book book = Book
            .builder()
            .id(1L)
            .title("Test Book1")
            .user(user1)
            .category(Category.CATEGORY_CS)
            .secret(false)
            .createdAt(LocalDateTime.now().minusDays(1))
            .build();

        bookRepository.save(book);

        // 댓글 객체 생성 및 저장
        BookComment comment = BookComment.builder()
            .comment("원래 댓글")
            .user(user1)
            .book(book)
            .createdAt(LocalDateTime.now())
            .build();
        bookCommentRepository.save(comment);

        // when (테스트 진행할 범위)
        comment.updateComment("수정된 댓글"); // 댓글 수정
        bookCommentRepository.save(comment); // 수정된 댓글 저장

        // then (범위에 대한 결과 검증)
        BookComment updatedComment = bookCommentRepository.findById(comment.getId())
            .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        assertThat(updatedComment.getComment()).isEqualTo("수정된 댓글");
    }

    @DisplayName("문제집 댓글 삭제")
    @Test
    public void deleteCommentTest() {
        // given (사전 준비)
        Long bookId = 4L;

        // when (테스트 진행할 범위)
        bookCommentRepository.deleteById(bookId);

        // then (범위에 대한 결과 검증)
        Optional<BookComment> findBookComment = bookCommentRepository.findById(bookId);
        Assertions.assertThat(findBookComment).isNotPresent();
    }

}