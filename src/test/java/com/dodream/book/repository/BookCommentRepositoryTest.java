package com.dodream.book.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.dodream.book.entity.Book;
import com.dodream.book.entity.BookComment;
import com.dodream.common.enumtype.Category;
import com.dodream.user.entity.User;
import com.dodream.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
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

}