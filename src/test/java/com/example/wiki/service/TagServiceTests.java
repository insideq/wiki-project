package com.example.wiki.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.wiki.api.tag.TagRq;
import com.example.wiki.api.tag.TagRs;
import com.example.wiki.error.AlreadyExistsException;
import com.example.wiki.error.NotFoundException;

@SpringBootTest
@Transactional
class TagServiceTests {

    @Autowired
    private TagService tagService;

    @Test
    void create_shouldReturnTag() {
        final TagRs tag = tagService.create(new TagRq("Spring"));

        assertThat(tag).isNotNull();
        assertThat(tag.id()).isNotNull();
        assertThat(tag.name()).isEqualTo("Spring");
    }

    @Test
    void create_shouldThrowIfDuplicate() {
        tagService.create(new TagRq("Spring"));

        assertThatThrownBy(() -> tagService.create(new TagRq("Spring")))
                .isInstanceOf(AlreadyExistsException.class);
    }

    @Test
    void get_shouldReturnTag() {
        final TagRs created = tagService.create(new TagRq("Java"));

        final TagRs found = tagService.get(created.id());

        assertThat(found).isNotNull();
        assertThat(found.id()).isEqualTo(created.id());
        assertThat(found.name()).isEqualTo("Java");
    }

    @Test
    void get_shouldThrowIfNotFound() {
        assertThatThrownBy(() -> tagService.get(99999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getAll_shouldReturnList() {
        tagService.create(new TagRq("Spring"));
        tagService.create(new TagRq("Java"));
        tagService.create(new TagRq("Wiki"));

        final List<TagRs> all = tagService.getAll();

        assertThat(all).hasSizeGreaterThanOrEqualTo(3);
        assertThat(all.stream().map(TagRs::name))
                .contains("Spring", "Java", "Wiki");
    }

    @Test
    void delete_shouldRemoveTag() {
        final TagRs created = tagService.create(new TagRq("Temporary"));

        final TagRs deleted = tagService.delete(created.id());

        assertThat(deleted.id()).isEqualTo(created.id());
        assertThatThrownBy(() -> tagService.get(created.id()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void delete_shouldThrowIfNotFound() {
        assertThatThrownBy(() -> tagService.delete(99999L))
                .isInstanceOf(NotFoundException.class);
    }
}