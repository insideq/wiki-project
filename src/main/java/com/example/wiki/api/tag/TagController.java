package com.example.wiki.api.tag;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.*;

import com.example.wiki.configuration.Constants;
import com.example.wiki.service.TagService;

import jakarta.validation.Valid;

@Profile("front")
@RestController
@RequestMapping(Constants.API_URL + TagController.URL)
public class TagController {
    public static final String URL = "/tag";

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public List<TagRs> getAll() {
        return tagService.getAll();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public TagRs get(@PathVariable Long id) {
        return tagService.get(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public TagRs create(@RequestBody @Valid TagRq dto) {
        return tagService.create(dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public TagRs delete(@PathVariable Long id) {
        return tagService.delete(id);
    }
}