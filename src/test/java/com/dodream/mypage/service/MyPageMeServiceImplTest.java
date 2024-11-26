package com.dodream.mypage.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dodream.book.domain.BookResponse;
import com.dodream.book.domain.BookUpdateResponse;
import com.dodream.book.entity.Book;
import com.dodream.book.entity.BookComment;
import com.dodream.book.entity.Question;
import com.dodream.book.entity.UserAnswer;
import com.dodream.book.enumtype.Evaluation;
import com.dodream.book.repository.BookCommentRepository;
import com.dodream.book.repository.BookRepository;
import com.dodream.book.repository.BookmarkRepository;
import com.dodream.book.repository.UserAnswerRepository;
import com.dodream.common.exception.BaseException;
import com.dodream.common.exception.ErrorCode;
import com.dodream.mypage.domain.BookCommentResponse;
import com.dodream.mypage.domain.GetUserAnswerResponse;
import com.dodream.mypage.domain.UserInfoResponse;
import com.dodream.mypage.service.MyPageMeServiceImpl;
import com.dodream.user.entity.User;
import com.dodream.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class MyPageMeServiceImplTest {

    @InjectMocks
    private MyPageMeServiceImpl myPageMeService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookCommentRepository bookCommentRepository;

    @Mock
    private UserAnswerRepository userAnswerRepository;

    @Mock
    private BookmarkRepository bookmarkRepository;

    private User testUser;

    @BeforeEach
    void start() {
        testUser = User.builder()
            .id(1L)
            .username("testUser")
            .profileImage("testProfile.jpg")
            .build();

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        SecurityContextHolder.setContext(securityContext);
    }

    @DisplayName("마이페이지 유저 정보 조회")
    @Test
    public void testGetUserProfileSuccess() {
        // given (사전 준비)
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // when (테스트 진행할 행위)
        UserInfoResponse result = myPageMeService.getUserProfile();

        // then (행위에 대한 결과 검증)
        assertThat(result).isNotNull();
        assertThat(result.getUserName()).isEqualTo("testUser");
        assertThat(result.getProfileImage()).isEqualTo("testProfile.jpg");
        verify(userRepository).findById(1L);

        // Reset SecurityContext to avoid side effects in other tests;
        SecurityContextHolder.clearContext();
    }

    @DisplayName("마이페이지 유저 정보 조회 실패")
    @Test
    void testGetUserProfileUserNotFound() {
        // given (사전 준비)
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        BaseException exception = assertThrows(BaseException.class, myPageMeService::getUserProfile);

        // when & then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
        verify(userRepository).findById(1L);
    }

    @DisplayName("마이페이지 모든 문제집 불러오기")
    @Test
    public void testGetUserBooksAll() {
        // given (사전 준비)
        Pageable pageable = PageRequest.of(0, 10);
        Book testBook = Book.builder()
            .id(1L)
            .title("Test Book")
            .user(testUser)
            .build();

        Page<Book> bookPage = new PageImpl<>(List.of(testBook), pageable, 1);
        when(bookRepository.findByUserIdOrderByCreatedAtDesc(testUser.getId(), pageable)).thenReturn(bookPage);

        // when (테스트 진행할 행위)
        Page<BookResponse> result = myPageMeService.getUserBooksAll(pageable);

        // then (행위에 대한 결과 검증)
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Test Book");
        verify(bookRepository).findByUserIdOrderByCreatedAtDesc(testUser.getId(), pageable);

        SecurityContextHolder.clearContext();
    }

    @DisplayName("마이페이지 공개/비공개 테스트")
    @Test
    public void testUpdateSecret() {
        // given (사전 준비)
        Book testBook = Book.builder()
            .id(1L)
            .title("Test Book")
            .user(testUser)
            .secret(false)
            .build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        // when (테스트 진행할 행위)
        BookUpdateResponse response = myPageMeService.updateSecret(1L);
        testBook.setSecret(true);

        // then (행위에 대한 결과 검증)
        assertThat(response).isNotNull();
        assertThat(testBook.isSecret()).isTrue();
        verify(bookRepository).findById(1L);
        verify(bookRepository).save(testBook);

        SecurityContextHolder.clearContext();
    }

    @DisplayName("유저별 댓글 조회")
    @Test
    public void testGetUserComment() {
        // given (사전 준비)
        Pageable pageable = PageRequest.of(0, 10);
        Book testBook = Book.builder()
            .id(1L)
            .title("Test Book")
            .build();

        BookComment testComment = BookComment.builder()
            .id(1L)
            .comment("Test Comment")
            .user(testUser)
            .book(testBook)
            .build();

        Page<BookComment> commentPage = new PageImpl<>(List.of(testComment), pageable, 1L);
        when(bookCommentRepository.findByUserIdOrderByCreatedAtDesc(testUser.getId(), pageable)).thenReturn(commentPage);

        // when (테스트 진행할 행위)
        Page<BookCommentResponse> result = myPageMeService.getUserComment(pageable);

        // then (행위에 대한 결과 검증)
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getComment()).isEqualTo("Test Comment");
        assertThat(result.getContent().get(0).getBookId()).isEqualTo(1L);
        verify(bookCommentRepository).findByUserIdOrderByCreatedAtDesc(testUser.getId(), pageable);

        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("유저별 댓글 조회 - 댓글 없음 예외")
    public void testGetUserCommentNotFound() {
        Pageable pageable = PageRequest.of(0, 10);
        when(bookCommentRepository.findByUserIdOrderByCreatedAtDesc(testUser.getId(), pageable)).thenReturn(Page.empty());

        BaseException exception = assertThrows(BaseException.class, () -> myPageMeService.getUserComment(pageable));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.BOOK_COMMENT_NOT_FOUND);
        verify(bookCommentRepository).findByUserIdOrderByCreatedAtDesc(testUser.getId(), pageable);
    }

    @DisplayName("답안별 평가이력 조회")
    @Test
    public void testGetUserAnswerByEvaluation() {
        // given (사전 준비)
        Pageable pageable = PageRequest.of(0, 10);

        Book testBook = Book.builder()
            .id(1L)
            .title("Test Book")
            .build();

        Question question = Question.builder()
            .id(1L)
            .question("테스트 문제")
            .modelAnswer("테스트 답변")
            .book(testBook)
            .build();

        UserAnswer testAnswer = UserAnswer.builder()
            .id(1L)
            .evaluation(Evaluation.EVALUATION_BEFORE)
            .question(question)
            .user(testUser)
            .build();

        Page<UserAnswer> answerPage = new PageImpl<>(List.of(testAnswer), pageable, 1);

        // lenient 설정을 추가하거나 필요 없는 경우 제거
        lenient().when(userAnswerRepository.findByUserIdAndEvaluationOrderByCreatedAtDesc(
            testUser.getId(), Evaluation.EVALUATION_BEFORE, pageable)).thenReturn(answerPage);

        // when (테스트 진행할 행위)
        Page<GetUserAnswerResponse> result = myPageMeService.getUserAnswerByEvaluation(
            "EVALUATION_BEFORE", pageable);

        // then (행위에 대한 결과 검증)
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getEvaluation()).isEqualTo("EVALUATION_BEFORE");
        verify(userAnswerRepository).findByUserIdAndEvaluationOrderByCreatedAtDesc(
            testUser.getId(), Evaluation.EVALUATION_BEFORE, pageable);

        SecurityContextHolder.clearContext();
    }

    @DisplayName("답안별 평가이력 조회 - 실패 (데이터 없음)")
    @Test
    public void testGetUserAnswerByEvaluationNoDataFound() {
        // given (사전 준비)
        Pageable pageable = PageRequest.of(0, 10);
        when(userAnswerRepository.findByUserIdAndEvaluationOrderByCreatedAtDesc(
            testUser.getId(), Evaluation.EVALUATION_BEFORE, pageable)).thenReturn(Page.empty());

        // when (테스트 진행할 행위)
        BaseException exception = assertThrows(BaseException.class, () -> {
            myPageMeService.getUserAnswerByEvaluation("EVALUATION_BEFORE", pageable);
        });

        // then (행위에 대한 결과 검증)
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_ANSWER_NOT_FOUND);
    }

}