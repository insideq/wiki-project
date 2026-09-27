package com.example.wiki.api.wiki;

import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.wiki.api.PageHelper;
import com.example.wiki.api.PageRs;
import com.example.wiki.configuration.Constants;
import com.example.wiki.entity.UserEntity;
import com.example.wiki.security.UserPrincipal;
import com.example.wiki.service.WikiPageService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

@Profile("front")
@RestController
@RequestMapping(Constants.API_URL + WikiPageController.URL)
public class WikiPageController {
    public static final String URL = "/wiki";

    private final WikiPageService wikiPageService;

    public WikiPageController(WikiPageService wikiPageService) {
        this.wikiPageService = wikiPageService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public PageRs<WikiPageRs> search(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long tagId,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size) {
        return wikiPageService.search(query, tagId, PageHelper.toPageable(page, size));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public WikiPageRs get(@PathVariable Long id) {
        return wikiPageService.get(id);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public WikiPageRs create(
            @RequestBody @Valid WikiPageRq dto,
            @AuthenticationPrincipal UserPrincipal principal) {
        final UserEntity user = principal.getUser();
        return wikiPageService.create(dto, user);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public WikiPageRs update(
            @PathVariable Long id,
            @RequestBody @Valid WikiPageRq dto,
            @AuthenticationPrincipal UserPrincipal principal) {
        final UserEntity user = principal.getUser();
        return wikiPageService.update(id, dto, user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public WikiPageRs delete(@PathVariable Long id) {
        return wikiPageService.delete(id);
    }
}