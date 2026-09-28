package com.example.wiki.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.annotation.Transactional;

import com.example.wiki.api.user.UserLoginRq;
import com.example.wiki.api.user.UserRq;

@SpringBootTest
@Transactional
class AuthServiceTests {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    private static final String LOGIN = "auth_user";
    private static final String PASSWORD = "auth_pass123";

    @BeforeEach
    void setUp() {
        if (userService.getAll(org.springframework.data.domain.PageRequest.of(0, 100))
                .items().stream()
                .noneMatch(u -> u.login().equals(LOGIN))) {
            userService.create(new UserRq(LOGIN, PASSWORD, PASSWORD));
        }
    }

    @Test
    void authenticate_shouldReturnToken() {
        final String token = authService.authenticate(new UserLoginRq(LOGIN, PASSWORD));

        assertThat(token).isNotNull();
        assertThat(token).isNotBlank();
    }

    @Test
    void authenticate_shouldThrowIfPasswordWrong() {
        assertThatThrownBy(() -> authService.authenticate(new UserLoginRq(LOGIN, "wrong")))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void authenticate_shouldThrowIfUserNotFound() {
        assertThatThrownBy(() -> authService.authenticate(new UserLoginRq("ghost", "any")))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void logout_shouldBlacklistToken() {
        final String token = authService.authenticate(new UserLoginRq(LOGIN, PASSWORD));

        assertThat(authService.isTokenBlacklisted(token)).isFalse();

        authService.logout(token);

        assertThat(authService.isTokenBlacklisted(token)).isTrue();
    }
}