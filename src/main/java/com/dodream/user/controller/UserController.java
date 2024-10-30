package com.dodream.user.controller;

import com.dodream.user.domain.AuthEnum;
import com.dodream.user.domain.LoginResponse;
import com.dodream.user.entity.User;
import com.dodream.user.service.UserService;
import com.dodream.util.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final TokenUtils tokenUtils;

    @PostMapping("/oauth/{provider}")
    public ResponseEntity<?> oAuthUserCheck(@RequestParam("code") final String code,
        @PathVariable("provider") final String provider, HttpServletResponse res) {
        log.info("들어온 코드 값 > {}, {}", code, provider);
        AuthEnum auth = AuthEnum.fromString(provider);
        log.info(auth.toString());
        User user = userService.oAuthUser(code, auth);
        String accessToken = userService.login(user, res);
        LoginResponse loginResponse = LoginResponse
                .builder()
                .accessToken(accessToken)
                .build();
        return ResponseEntity.ok(loginResponse);
    }

    // token 재발급
    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponse> getRefreshToken(HttpServletResponse response, HttpServletRequest request) {
        // token 요청
        Map<String, String> newTokenMap = userService.refreshToken(request);

        // token 재발급 불가인 경우 401 error
        if (newTokenMap == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        // refresh token 재발급
        tokenUtils.setRefreshTokenCookie(response, newTokenMap.get("refreshToken"));

        return ResponseEntity.ok(LoginResponse.builder().accessToken(newTokenMap.get("accessToken")).build());
    }

}
