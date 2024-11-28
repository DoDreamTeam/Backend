package com.dodream.book.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.dodream.book.entity.Book;
import com.dodream.book.entity.Bookmark;
import com.dodream.common.enumtype.Category;
import com.dodream.user.entity.User;
import com.dodream.user.repository.UserRepository;
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
class BookmarkRepositoryTest {

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    private User user;
    private Book book;

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
    }

    @DisplayName("북마크 수 카운트 테스트")
    @Test
    public void countByBookAndIsDeletedFalseTest() {
        // given
        Bookmark bookmark1 = Bookmark.builder()
            .user(user)
            .book(book)
            .isDeleted(false)
            .build();
        Bookmark bookmark2 = Bookmark.builder()
            .user(user)
            .book(book)
            .isDeleted(false)
            .build();
        bookmarkRepository.save(bookmark1);
        bookmarkRepository.save(bookmark2);

        // when
        long count = bookmarkRepository.countByBookAndIsDeletedFalse(book);

        // then
        assertThat(count).isEqualTo(2);
    }

    @DisplayName("북마크 조회 테스트")
    @Test
    public void findByUserAndBookTest() {
        // given
        Bookmark bookmark = Bookmark.builder()
            .user(user)
            .book(book)
            .isDeleted(false)
            .build();
        bookmarkRepository.save(bookmark);

        // when
        Optional<Bookmark> foundBookmark = bookmarkRepository.findByUserAndBook(user, book);

        // then
        assertThat(foundBookmark).isPresent();
        assertThat(foundBookmark.get()).isEqualTo(bookmark);
    }

    @DisplayName("삭제된 북마크는 카운트되지 않아야 한다")
    @Test
    public void countByBookAndIsDeletedFalseShouldNotCountDeletedBookmarks() {
        // given
        Bookmark deletedBookmark = Bookmark.builder()
            .user(user)
            .book(book)
            .isDeleted(true)
            .build();
        bookmarkRepository.save(deletedBookmark);

        // when
        long count = bookmarkRepository.countByBookAndIsDeletedFalse(book);

        // then
        assertThat(count).isEqualTo(0);
    }

    @DisplayName("유저 북마크 목록 조회 테스트")
    @Test
    public void findByUserIdOrderByBookCreatedAtDescTest() {
        // given
        Bookmark bookmark1 = Bookmark.builder()
            .user(user)
            .book(book)
            .isDeleted(false)
            .build();
        Bookmark bookmark2 = Bookmark.builder()
            .user(user)
            .book(book)
            .isDeleted(false)
            .build();
        bookmarkRepository.save(bookmark1);
        bookmarkRepository.save(bookmark2);

        // when
        var bookmarksPage = bookmarkRepository.findByUserIdAndIsDeletedFalseOrderByBookCreatedAtDesc(user.getId(), PageRequest.of(0, 10));

        // then
        assertThat(bookmarksPage.getContent()).hasSize(2);
    }

    @DisplayName("문제집으로 북마크 삭제 테스트")
    @Test
    public void deleteByBookTest() {
        // given
        Bookmark bookmark1 = Bookmark.builder()
            .user(user)
            .book(book)
            .isDeleted(false)
            .build();
        Bookmark bookmark2 = Bookmark.builder()
            .user(user)
            .book(book)
            .isDeleted(false)
            .build();
        bookmarkRepository.save(bookmark1);
        bookmarkRepository.save(bookmark2);

        // when
        bookmarkRepository.deleteByBook(book);

        // then
        long count = bookmarkRepository.countByBookAndIsDeletedFalse(book);
        assertThat(count).isEqualTo(0); // 모든 북마크가 삭제되어야 함
    }
}
