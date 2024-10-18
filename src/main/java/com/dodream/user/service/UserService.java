package com.dodream.user.service;

import com.dodream.user.domain.AuthEnum;
import com.dodream.user.entity.User;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {

    User oAuthUser(String code, AuthEnum auth);

    String login(User user, HttpServletResponse res);
}
