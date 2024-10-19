package com.dodream.security;

import static java.util.Collections.emptyMap;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
public class JwtFactory {

    private String subject = "codesche";
    private Date issuedAt = new Date();
    private Date expiration = new Date(new Date().getTime() + Duration.ofDays(14).toMillis());
    private Map<String, Object> claims = emptyMap();

    // 빌더 패턴 활용하여 설정이 필요한 데이터만 선택 설정
    @Builder
    public JwtFactory(String subject, Date issuedAt, Date expiration,
        Map<String, Object> claims) {
        this.subject = subject != null ? subject : this.subject;
        this.issuedAt = issuedAt != null ? issuedAt : this.issuedAt;
        this.expiration = expiration != null ? expiration : this.expiration;
        this.claims = claims != null ? claims : this.claims;
    }

    public static JwtFactory withDefaultValues() {
        return JwtFactory.builder().build();
    }

    // jjwt 라이브러리를 사용해 JWT 토큰 생성
    public String createToken(JwtProperties jwtProperties) {
        // JWT 타입을 명시
        // 발행자 정보 설정
        // 발행일시 설정
        // 만료 시간 설정
        // 토큰의 주제(Subject) 설정
        return Jwts.builder()
            .header().add("typ", "JWT") // JWT 타입을 명시
            .and()
            .issuer(jwtProperties.getIssuer())          // 발행자 정보 설정
            .issuedAt(new Date())                       // 발행일시 설정
            .expiration(expiration)                     // 만료 시간 설정
            .subject(subject)                           // 토큰의 주제(Subject) 설정
            .claim("username", subject)
            .signWith(Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes()), Jwts.SIG.HS256) // 비밀키와 해시 알고리즘 사용하여 토큰 설명값 설정
            .compact(); // 토큰 정보들을 최종적으로 압축해서 문자열로 반환
    }

}
