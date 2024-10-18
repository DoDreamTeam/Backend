package com.dodream.common.config;

import com.dodream.security.JwtAuthenticationFilter;
import com.dodream.security.JwtProperties;
import com.dodream.security.JwtProvider;
import com.dodream.user.repository.UserRepository;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final UserRepository userRepository;
    private final JwtProperties jwtProperties;

    // JWT PROVIDER 생성자 호출
    private JwtProvider jwtProvider() {
        return new JwtProvider(userRepository, jwtProperties);
    }

    // HTTP 요청에 따른 보안 구성
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 경로 권한 설정
        http.authorizeHttpRequests(auth ->
            // 특정 URL 경로에 대해서는 인증 없이 접근 가능
            auth.requestMatchers(
                    // 로그인/로그아웃
                    new AntPathRequestMatcher("/api/oauth/**"),

                    // 모든 문제집 조회 (최신순)
                    new AntPathRequestMatcher("/api/books", "GET"),

                    // 특정 문제집의 전체 문제 조회
                    new AntPathRequestMatcher("/api/books/**/questions", "GET"),

                    // 특정 문제집의 모든 댓글 조회
                    new AntPathRequestMatcher("/api/books/*/comments", "GET"),

                    // 모든 스터디 조회
                    new AntPathRequestMatcher("/api/study", "GET"),

                    // 특정 스터디방의 전체 문제 조회 (최신순)
                    new AntPathRequestMatcher("/api/study/*/studyroom", "GET"),

                    // 특정 스터디방의 공지사항 조회
                    new AntPathRequestMatcher("/api/study/*/notice", "GET"),

                    // 특정 스터디방 공지사항의 모든 댓글, 좋아요 조회
                    new AntPathRequestMatcher("/api/notice/*/comments", "GET"),

                    // 특정 스터디의 문제 조회
                    new AntPathRequestMatcher("/api/study/*/question/*", "GET")
                ).permitAll()
                // 그 밖의 다른 요청들은 인증을 통과한(로그인한) 사용자라면 모두 접근할 수 있도록 한다.
                .anyRequest().authenticated()
        );

        // 무상태성 세션 관리
        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // (토큰을 통해 검증할 수 있도록) 필터 추가 [jwtProvider 생성자를 호출해 매개변수로 등록]
        http.addFilterAfter(new JwtAuthenticationFilter(jwtProvider()),
            UsernamePasswordAuthenticationFilter.class);

        // HTTP 기본 설정
        http.httpBasic(HttpBasicConfigurer::disable);

        // CSRF 비활성화
        http.csrf(AbstractHttpConfigurer::disable);

        // CORS 설정
        http.cors(corsConfig -> corsConfig.configurationSource(corsConfigurationSource()));

        return http.getOrBuild();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedHeaders(Collections.singletonList("*"));
            config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
            config.setAllowedOriginPatterns(List.of("http://localhost:3000", "http://localhost:8080"
                , "http://localhost:5173"));
            config.setAllowCredentials(true);
            return config;
        };
    }

}
