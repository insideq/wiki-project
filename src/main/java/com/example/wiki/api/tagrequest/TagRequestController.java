package com.example.wiki.api.tagrequest;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.wiki.configuration.Constants;
import com.example.wiki.security.UserPrincipal;
import com.example.wiki.service.TagRequestService;

import jakarta.validation.Valid;

@Profile("front")
@RestController
@RequestMapping(Constants.API_URL + TagRequestController.URL)
public class TagRequestController {
    public static final String URL = "/tag-request";

    private final TagRequestService tagRequestService;

    public TagRequestController(TagRequestService tagRequestService) {
        this.tagRequestService = tagRequestService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<TagRequestRs> getAllPending() {
        return tagRequestService.getAllPending();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public TagRequestRs create(
            @RequestBody @Valid TagRequestRq dto,
            @AuthenticationPrincipal UserPrincipal principal) {
        return tagRequestService.create(dto, principal.getUser());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/approve")
    public TagRequestRs approve(@PathVariable Long id) {
        return tagRequestService.approve(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/reject")
    public TagRequestRs reject(@PathVariable Long id) {
        return tagRequestService.reject(id);
    }
}