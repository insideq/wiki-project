package com.example.wiki.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.wiki.api.tagrequest.TagRequestRq;
import com.example.wiki.api.tagrequest.TagRequestRs;
import com.example.wiki.entity.UserEntity;
import com.example.wiki.entity.UserRole;
import com.example.wiki.error.NotFoundException;
import com.example.wiki.repository.UserRepository;

@SpringBootTest
@Transactional
class TagRequestServiceTests {

    @Autowired
    private TagRequestService tagRequestService;

    @Autowired
    private TagService tagService;

    @Autowired
    private UserRepository userRepository;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        user = new UserEntity("requester", "pass");
        user.setRole(UserRole.USER);
        user = userRepository.save(user);
    }

    @Test
    void create_shouldReturnPendingRequest() {
        final TagRequestRs request = tagRequestService.create(new TagRequestRq("NewTag"), user);

        assertThat(request).isNotNull();
        assertThat(request.id()).isNotNull();
        assertThat(request.name()).isEqualTo("NewTag");
        assertThat(request.requestedBy()).isEqualTo("requester");
        assertThat(request.status()).isEqualTo("PENDING");
    }

    @Test
    void getAllPending_shouldReturnOnlyPending() {
        tagRequestService.create(new TagRequestRq("Tag1"), user);
        tagRequestService.create(new TagRequestRq("Tag2"), user);

        final List<TagRequestRs> pending = tagRequestService.getAllPending();

        assertThat(pending).hasSizeGreaterThanOrEqualTo(2);
        assertThat(pending).allMatch(r -> "PENDING".equals(r.status()));
    }

    @Test
    void approve_shouldCreateTagAndMarkApproved() {
        final TagRequestRs request = tagRequestService.create(new TagRequestRq("ApprovedTag"), user);

        final TagRequestRs approved = tagRequestService.approve(request.id());

        assertThat(approved.status()).isEqualTo("APPROVED");

        final List<TagRequestRs> pending = tagRequestService.getAllPending();
        assertThat(pending).noneMatch(r -> r.id().equals(request.id()));
    }

    @Test
    void reject_shouldMarkRejected() {
        final TagRequestRs request = tagRequestService.create(new TagRequestRq("RejectedTag"), user);

        final TagRequestRs rejected = tagRequestService.reject(request.id());

        assertThat(rejected.status()).isEqualTo("REJECTED");
    }

    @Test
    void approve_shouldThrowIfNotFound() {
        assertThatThrownBy(() -> tagRequestService.approve(99999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void reject_shouldThrowIfNotFound() {
        assertThatThrownBy(() -> tagRequestService.reject(99999L))
                .isInstanceOf(NotFoundException.class);
    }
}