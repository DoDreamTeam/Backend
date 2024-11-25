package com.dodream.mypage.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dodream.common.exception.BaseException;
import com.dodream.common.exception.ErrorCode;
import com.dodream.mypage.domain.UserInfoResponse;
import com.dodream.user.entity.User;
import com.dodream.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MyPageServiceImplTest {

    @InjectMocks
    private MyPageServiceImpl myPageService;

    @Mock
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .id(1L)
            .username("testUser")
            .profileImage("https://s3.amazonaws.com/test-bucket/old-profile.jpg")
            .build();
    }

    @DisplayName("마이페이지 유저 정보 조회")
    @Test
    public void getUserInfo() {
        // given (사전 준비)
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // when (테스트 진행할 행위)
        UserInfoResponse response = myPageService.getUserInfo(1L);

        // then (행위에 대한 결과 검증)
        assertNotNull(response);
        assertEquals("testUser", response.getUserName());
        verify(userRepository, times(1)).findById(1L);
    }

    @DisplayName("마이페이지 유저 정보 조회 - 예외 케이스 (유저가 존재하지 않는 경우)")
    @Test
    public void getUserInfoUserNotFound() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        BaseException exception = assertThrows(BaseException.class,
            () -> myPageService.getUserInfo(1L));
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

}

