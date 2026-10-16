package com.example.wiki.api;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.wiki.api.user.UserLoginRq;
import com.example.wiki.api.user.UserRq;
import com.example.wiki.api.wiki.WikiPageRq;
import com.example.wiki.entity.UserEntity;
import com.example.wiki.entity.UserRole;
import com.example.wiki.entity.WikiPageEditPolicy;
import com.example.wiki.entity.WikiPageVisibility;
import com.example.wiki.repository.UserRepository;
import com.example.wiki.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class WikiPageControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private static final String WIKI_URL = "/api/1.0/wiki";
    private static final String LOGIN_URL = "/api/1.0/auth/login";
    private static final String AUTHOR_LOGIN = "api_wiki_author";
    private static final String AUTHOR_PASSWORD = "pass123";

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        // Создаём пользователя-автора (если нет)
        try {
            userService.create(new UserRq(AUTHOR_LOGIN, AUTHOR_PASSWORD, AUTHOR_PASSWORD));
        } catch (Exception ignored) {
        }

        // Логинимся и получаем реальный JWT
        final String loginBody = objectMapper.writeValueAsString(
                new UserLoginRq(AUTHOR_LOGIN, AUTHOR_PASSWORD));

        token = mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void getAll_withoutAuth_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get(WIKI_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAll_asAuthenticated_shouldReturnPage() throws Exception {
        createTestPage("API Test Page");

        mockMvc.perform(get(WIKI_URL)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    void search_shouldFilterByQuery() throws Exception {
        createTestPage("Spring Guide");
        createTestPage("Java Guide");

        mockMvc.perform(get(WIKI_URL + "?query=Spring")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    void get_shouldReturnPage() throws Exception {
        final var page = createTestPage("Viewable Page");

        mockMvc.perform(get(WIKI_URL + "/" + page.id())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Viewable Page"));
    }

    @Test
    void get_withNonexistentId_shouldReturn404() throws Exception {
        mockMvc.perform(get(WIKI_URL + "/99999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_withoutAuth_shouldReturnUnauthorized() throws Exception {
        final String body = objectMapper.writeValueAsString(defaultRq("Unauthorized"));

        mockMvc.perform(post(WIKI_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void create_shouldReturnCreatedPage() throws Exception {
        final String body = objectMapper.writeValueAsString(defaultRq("Created via API"));

        mockMvc.perform(post(WIKI_URL)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Created via API"))
                .andExpect(jsonPath("$.visibility").value("PUBLIC"))
                .andExpect(jsonPath("$.editPolicy").value("OPEN"))
                .andExpect(jsonPath("$.authorLogin").value(AUTHOR_LOGIN));
    }

    @Test
    void create_withInvalidData_shouldReturn400() throws Exception {
        final String body = objectMapper.writeValueAsString(
                new WikiPageRq(null, null, null, null, null));

        mockMvc.perform(post(WIKI_URL)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    // ========== Вспомогательные методы ==========

    private com.example.wiki.api.wiki.WikiPageRs createTestPage(String title) {
        final UserEntity author = userRepository.findByLoginIgnoreCase(AUTHOR_LOGIN)
                .orElseThrow();
        try {
            final String body = objectMapper.writeValueAsString(defaultRq(title));
            final String response = mockMvc.perform(post(WIKI_URL)
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            return objectMapper.readValue(response, com.example.wiki.api.wiki.WikiPageRs.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private WikiPageRq defaultRq(String title) {
        return new WikiPageRq(
                title,
                "# Content\n\nTest",
                WikiPageVisibility.PUBLIC,
                WikiPageEditPolicy.OPEN,
                Set.of());
    }
}