package com.dodream.user.controller;

import com.dodream.user.domain.AuthEnum;
import com.dodream.user.domain.LoginResponse;
import com.dodream.user.entity.User;
import com.dodream.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/oauth/{provider}")
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
}
