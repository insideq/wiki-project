package com.example.wiki.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.wiki.api.deleterequest.DeleteRequestRs;
import com.example.wiki.api.wiki.WikiPageRq;
import com.example.wiki.api.wiki.WikiPageRs;
import com.example.wiki.entity.UserEntity;
import com.example.wiki.entity.UserRole;
import com.example.wiki.entity.WikiPageEditPolicy;
import com.example.wiki.entity.WikiPageVisibility;
import com.example.wiki.error.NotFoundException;
import com.example.wiki.repository.UserRepository;

@SpringBootTest
@Transactional
class DeleteRequestServiceTests {

    @Autowired
    private DeleteRequestService deleteRequestService;

    @Autowired
    private WikiPageService wikiPageService;

    @Autowired
    private UserRepository userRepository;

    private UserEntity author;

    @BeforeEach
    void setUp() {
        author = new UserEntity("author_del", "pass");
        author.setRole(UserRole.USER);
        author = userRepository.save(author);
    }

    private WikiPageRs createPage(String title) {
        final WikiPageRq rq = new WikiPageRq(
                title, "content",
                WikiPageVisibility.PUBLIC,
                WikiPageEditPolicy.OPEN,
                Set.of());
        return wikiPageService.create(rq, author);
    }

    @Test
    void create_shouldReturnPendingRequest_andMarkPageAsPendingDeletion() {
        final WikiPageRs page = createPage("To delete");

        final DeleteRequestRs request = deleteRequestService.create(page.id(), author);

        assertThat(request).isNotNull();
        assertThat(request.pageId()).isEqualTo(page.id());
        assertThat(request.pageTitle()).isEqualTo("To delete");
        assertThat(request.status()).isEqualTo("PENDING");

        final WikiPageRs updatedPage = wikiPageService.get(page.id());
        assertThat(updatedPage.status()).isEqualTo("PENDING_DELETION");
    }

    @Test
    void approve_shouldDeletePage() {
        final WikiPageRs page = createPage("Delete approved");
        final DeleteRequestRs request = deleteRequestService.create(page.id(), author);

        final WikiPageRs deleted = deleteRequestService.approve(request.id());

        assertThat(deleted.id()).isEqualTo(page.id());

        assertThatThrownBy(() -> wikiPageService.get(page.id()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void reject_shouldReturnPageToActive() {
        final WikiPageRs page = createPage("Delete rejected");
        final DeleteRequestRs request = deleteRequestService.create(page.id(), author);

        final DeleteRequestRs rejected = deleteRequestService.reject(request.id());

        assertThat(rejected.status()).isEqualTo("REJECTED");

        final WikiPageRs restored = wikiPageService.get(page.id());
        assertThat(restored.status()).isEqualTo("ACTIVE");
    }

    @Test
    void getAllPending_shouldReturnPendingRequests() {
        final WikiPageRs page1 = createPage("Page1");
        final WikiPageRs page2 = createPage("Page2");
        deleteRequestService.create(page1.id(), author);
        deleteRequestService.create(page2.id(), author);

        final var pending = deleteRequestService.getAllPending();

        assertThat(pending).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void approve_shouldThrowIfNotFound() {
        assertThatThrownBy(() -> deleteRequestService.approve(99999L))
                .isInstanceOf(NotFoundException.class);
    }
}