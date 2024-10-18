package com.dodream.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("jwt")     // application.yml에 설정한 jwt.issuer, jwt.secret_key,...이 매핑
public class JwtProperties {
    private String issuer;
    private String secretKey;
    private int accessDuration;
    private int refreshDuration;
}
