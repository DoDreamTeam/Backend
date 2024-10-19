package com.dodream.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.dodream.user.entity.User;
import com.dodream.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class JwtProviderTest {

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtProperties jwtProperties;

    @DisplayName("유저 정보와 만료 기간을 전달해 토큰을 만들 수 있다.")
    @Test
    public void generateToken() {
        // given (사전 준비)
        User testUser = userRepository.save(User.builder()
                                .username("codesche")
                                .providerId("37114854051234567")
                                .provider("kakao")
                                .profileImage("https://lh3.googleusercontent.com/a/13124125dsafasfdasdfsaasf")
                                .build());

        // when (테스트 진행할 행위)
        String token = jwtProvider.generateAccessToken(testUser);

        // then (행위에 대한 결과 검증)
        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes()))
                .build()
                .parseSignedClaims(token)       // 서명된 클레임을 파싱..
                .getPayload();                  // 파싱된 클레임에서 페이로드(실제 클레임)를 반환

        assertThat(claims.get("sub", String.class)).isEqualTo(testUser.getUsername());
    }

    @DisplayName("만료된 토큰인 경우 유효성 검증 실패한다.")
    @Test
    public void validTokenFail() {
        // given (사전 준비)
        String token = JwtFactory.builder()
            .expiration(new Date(new Date().getTime() - Duration.ofDays(7).toMillis()))
            .build()
            .createToken(jwtProperties);

        // when (테스트 진행할 행위)
        boolean result = jwtProvider.validateToken(token);

        // then (행위에 대한 결과 검증)
        assertThat(result).isFalse();
    }

    @DisplayName("유효한 토큰인 경우 유효성 검증에 성공한다.")
    @Test
    public void validTokenSuccess() {
        // given (사전 준비)
        String token = JwtFactory.withDefaultValues()
                .createToken(jwtProperties);

        // when (테스트 진행할 행위)
        boolean result = jwtProvider.validateToken(token);

        // then (행위에 대한 결과 검증)
        assertThat(result).isTrue();
    }

    @DisplayName("토큰 기반으로 유저명을 가져올 수 있다.")
    @Test
    public void getUsername() {
        // given (사전 준비)
        String username = "codesche";
        String token = JwtFactory.builder()
                    .claims(Map.of("username", username))
                    .build()
                    .createToken(jwtProperties);

        // when (테스트 진행할 행위)
        String usernameByToken = jwtProvider.getUsernameByToken(token);

        // then (행위에 대한 결과 검증)
        assertThat(usernameByToken).isEqualTo(username);
    }

}