package com.example.wiki.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.wiki.api.tag.TagRq;
import com.example.wiki.service.TagService;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TagControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TagService tagService;

    private static final String TAG_URL = "/api/1.0/tag";

    @BeforeEach
    void setUp() {
        // создаём тестовый тег для тестов, которые требуют существующий
        try {
            tagService.create(new TagRq("api_test_tag"));
        } catch (Exception ignored) {
        }
    }

    @Test
    @WithMockUser(username = "test_user", roles = "USER")
    void getAll_asUser_shouldReturnList() throws Exception {
        mockMvc.perform(get(TAG_URL))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test_user", roles = "USER")
    void getAll_asAuthenticated_returnsJsonArray() throws Exception {
        mockMvc.perform(get(TAG_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getAll_withoutAuth_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get(TAG_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void create_asAdmin_shouldSucceed() throws Exception {
        final String body = objectMapper.writeValueAsString(new TagRq("new_tag_from_api"));

        mockMvc.perform(post(TAG_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("new_tag_from_api"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void create_asUser_shouldReturnForbidden() throws Exception {
        final String body = objectMapper.writeValueAsString(new TagRq("forbidden_tag"));

        mockMvc.perform(post(TAG_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void get_shouldReturnTag() throws Exception {
        final var existing = tagService.getAll().get(0);

        mockMvc.perform(get(TAG_URL + "/" + existing.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(existing.name()));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void delete_asAdmin_shouldSucceed() throws Exception {
        final var existing = tagService.getAll().get(0);

        mockMvc.perform(delete(TAG_URL + "/" + existing.id()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void delete_asUser_shouldReturnForbidden() throws Exception {
        final var existing = tagService.getAll().get(0);

        mockMvc.perform(delete(TAG_URL + "/" + existing.id()))
                .andExpect(status().isForbidden());
    }
}