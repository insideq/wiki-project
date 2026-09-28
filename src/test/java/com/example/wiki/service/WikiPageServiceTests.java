package com.example.wiki.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import com.example.wiki.api.PageRs;
import com.example.wiki.api.tag.TagRq;
import com.example.wiki.api.tag.TagRs;
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
class WikiPageServiceTests {

    @Autowired
    private WikiPageService wikiPageService;

    @Autowired
    private TagService tagService;

    @Autowired
    private UserRepository userRepository;

    private UserEntity author;
    private UserEntity otherUser;
    private UserEntity admin;

    @BeforeEach
    void setUp() {
        author = createUser("author", UserRole.USER);
        otherUser = createUser("other", UserRole.USER);
        admin = createUser("admin_test", UserRole.ADMIN);
    }

    private UserEntity createUser(String login, UserRole role) {
        final UserEntity user = new UserEntity(login, "encoded_pass");
        user.setRole(role);
        return userRepository.save(user);
    }

    private WikiPageRq defaultRq(String title, String content) {
        return new WikiPageRq(
                title,
                content,
                WikiPageVisibility.PUBLIC,
                WikiPageEditPolicy.OPEN,
                Set.of());
    }

    @Test
    void create_shouldReturnPage() {
        final WikiPageRs page = wikiPageService.create(defaultRq("Test", "# Hello"), author);

        assertThat(page).isNotNull();
        assertThat(page.id()).isNotNull();
        assertThat(page.title()).isEqualTo("Test");
        assertThat(page.content()).isEqualTo("# Hello");
        assertThat(page.authorLogin()).isEqualTo("author");
        assertThat(page.visibility()).isEqualTo("PUBLIC");
        assertThat(page.editPolicy()).isEqualTo("OPEN");
        assertThat(page.status()).isEqualTo("ACTIVE");
    }

    @Test
    void create_withTags_shouldReturnPageWithTags() {
        final TagRs tag = tagService.create(new TagRq("Spring"));

        final WikiPageRq rq = new WikiPageRq(
                "With tags",
                "content",
                WikiPageVisibility.PUBLIC,
                WikiPageEditPolicy.OPEN,
                Set.of(tag.id()));

        final WikiPageRs page = wikiPageService.create(rq, author);

        assertThat(page.tags()).hasSize(1);
        assertThat(page.tags().get(0).name()).isEqualTo("Spring");
    }

    @Test
    void get_shouldReturnPage() {
        final WikiPageRs created = wikiPageService.create(defaultRq("Title", "Content"), author);

        final WikiPageRs found = wikiPageService.get(created.id());

        assertThat(found.id()).isEqualTo(created.id());
        assertThat(found.title()).isEqualTo("Title");
        assertThat(found.content()).isEqualTo("Content");
    }

    @Test
    void get_shouldThrowIfNotFound() {
        assertThatThrownBy(() -> wikiPageService.get(99999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void search_shouldReturnActivePages() {
        wikiPageService.create(defaultRq("Spring guide", "content"), author);
        wikiPageService.create(defaultRq("Java guide", "content"), author);

        final PageRs<WikiPageRs> result = wikiPageService.search(
                null, null, PageRequest.of(0, 10));

        assertThat(result.items()).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void search_byQuery_shouldReturnMatchingPages() {
        wikiPageService.create(defaultRq("Spring guide", "content"), author);
        wikiPageService.create(defaultRq("Java guide", "content"), author);

        final PageRs<WikiPageRs> result = wikiPageService.search(
                "Spring", null, PageRequest.of(0, 10));

        assertThat(result.items()).hasSize(1);
        assertThat(result.items().get(0).title()).isEqualTo("Spring guide");
    }

    @Test
    void update_byAuthor_shouldSucceed() {
        final WikiPageRs created = wikiPageService.create(defaultRq("Old", "old"), author);

        final WikiPageRq update = defaultRq("New", "new content");
        final WikiPageRs updated = wikiPageService.update(created.id(), update, author);

        assertThat(updated.title()).isEqualTo("New");
        assertThat(updated.content()).isEqualTo("new content");
    }

    @Test
    void update_byOtherUser_onOpenPage_shouldSucceed() {
        final WikiPageRs created = wikiPageService.create(defaultRq("Open", "content"), author);

        final WikiPageRq update = defaultRq("Updated by other", "content");
        final WikiPageRs updated = wikiPageService.update(created.id(), update, otherUser);

        assertThat(updated.title()).isEqualTo("Updated by other");
    }

    @Test
    void update_byOtherUser_onOwnerOnly_shouldThrow() {
        final WikiPageRq rq = new WikiPageRq(
                "Private edit",
                "content",
                WikiPageVisibility.PUBLIC,
                WikiPageEditPolicy.OWNER_ONLY,
                Set.of());

        final WikiPageRs created = wikiPageService.create(rq, author);

        assertThatThrownBy(() -> wikiPageService.update(created.id(), defaultRq("Hacked", "hacked"), otherUser))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void update_byAdmin_onOwnerOnly_shouldSucceed() {
        final WikiPageRq rq = new WikiPageRq(
                "Admin edit",
                "content",
                WikiPageVisibility.PUBLIC,
                WikiPageEditPolicy.OWNER_ONLY,
                Set.of());

        final WikiPageRs created = wikiPageService.create(rq, author);

        final WikiPageRs updated = wikiPageService.update(created.id(), defaultRq("Admin updated", "content"), admin);

        assertThat(updated.title()).isEqualTo("Admin updated");
    }

    @Test
    void update_byOtherUser_onPrivatePage_shouldThrow() {
        final WikiPageRq rq = new WikiPageRq(
                "Private",
                "content",
                WikiPageVisibility.PRIVATE,
                WikiPageEditPolicy.OPEN,
                Set.of());

        final WikiPageRs created = wikiPageService.create(rq, author);

        assertThatThrownBy(() -> wikiPageService.update(created.id(), defaultRq("Hacked", "x"), otherUser))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void delete_shouldRemovePage() {
        final WikiPageRs created = wikiPageService.create(defaultRq("To delete", "x"), author);

        final WikiPageRs deleted = wikiPageService.delete(created.id());

        assertThat(deleted.id()).isEqualTo(created.id());
        assertThatThrownBy(() -> wikiPageService.get(created.id()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void delete_shouldThrowIfNotFound() {
        assertThatThrownBy(() -> wikiPageService.delete(99999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_shouldChangeTagsAndVisibility() {
        final TagRs tag1 = tagService.create(new TagRq("Tag1"));
        final TagRs tag2 = tagService.create(new TagRq("Tag2"));

        final WikiPageRq createRq = new WikiPageRq(
                "Page", "content",
                WikiPageVisibility.PUBLIC,
                WikiPageEditPolicy.OPEN,
                Set.of(tag1.id()));

        final WikiPageRs created = wikiPageService.create(createRq, author);
        assertThat(created.tags()).hasSize(1);

        final WikiPageRq updateRq = new WikiPageRq(
                "Page", "content",
                WikiPageVisibility.PRIVATE,
                WikiPageEditPolicy.OWNER_ONLY,
                Set.of(tag2.id()));

        final WikiPageRs updated = wikiPageService.update(created.id(), updateRq, author);

        assertThat(updated.visibility()).isEqualTo("PRIVATE");
        assertThat(updated.editPolicy()).isEqualTo("OWNER_ONLY");
        assertThat(updated.tags()).hasSize(1);
        assertThat(updated.tags().get(0).name()).isEqualTo("Tag2");
    }
}