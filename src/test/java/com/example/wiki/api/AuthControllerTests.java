package com.example.wiki.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.wiki.api.user.UserRq;
import com.example.wiki.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    private static final String LOGIN_URL = "/api/1.0/auth/login";
    private static final String SIGNUP_URL = "/api/1.0/auth/signup";
    private static final String WHOAMI_URL = "/api/1.0/auth/whoami";

    private static final String USER_LOGIN = "api_test_user";
    private static final String USER_PASSWORD = "test_password123";

    @BeforeEach
    void setUp() {
        try {
            userService.create(new UserRq(USER_LOGIN, USER_PASSWORD, USER_PASSWORD));
        } catch (Exception ignored) {
            // пользователь уже создан
        }
    }

    @Test
    void login_shouldReturnToken() throws Exception {
        final String body = objectMapper.writeValueAsString(
                new LoginRequest(USER_LOGIN, USER_PASSWORD));

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    void login_withWrongPassword_shouldReturnError() throws Exception {
        final String body = objectMapper.writeValueAsString(
                new LoginRequest(USER_LOGIN, "wrong_password"));

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void signup_shouldCreateUser() throws Exception {
        final String body = objectMapper.writeValueAsString(
                new UserRq("new_signup_user", "password123", "password123"));

        mockMvc.perform(post(SIGNUP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    void signup_withMismatchedPasswords_shouldReturnError() throws Exception {
        final String body = objectMapper.writeValueAsString(
                new UserRq("new_user2", "password123", "different"));

        mockMvc.perform(post(SIGNUP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whoami_withoutToken_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get(WHOAMI_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whoami_withValidToken_shouldReturnUser() throws Exception {
        // Логинимся и получаем токен
        final String loginBody = objectMapper.writeValueAsString(
                new LoginRequest(USER_LOGIN, USER_PASSWORD));

        final String token = mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Используем токен
        mockMvc.perform(get(WHOAMI_URL)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value(USER_LOGIN));
    }

    /**
     * Вспомогательный DTO для логина в тестах (чтобы не зависеть от основного UserLoginRq).
     */
    private record LoginRequest(String login, String password) {
    }
}