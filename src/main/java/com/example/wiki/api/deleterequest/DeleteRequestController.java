package com.example.wiki.api.deleterequest;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.wiki.api.wiki.WikiPageRs;
import com.example.wiki.configuration.Constants;
import com.example.wiki.security.UserPrincipal;
import com.example.wiki.service.DeleteRequestService;

@Profile("front")
@RestController
@RequestMapping(Constants.API_URL + DeleteRequestController.URL)
public class DeleteRequestController {
    public static final String URL = "/delete-request";

    private final DeleteRequestService deleteRequestService;

    public DeleteRequestController(DeleteRequestService deleteRequestService) {
        this.deleteRequestService = deleteRequestService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<DeleteRequestRs> getAllPending() {
        return deleteRequestService.getAllPending();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public DeleteRequestRs create(
            @RequestParam Long pageId,
            @AuthenticationPrincipal UserPrincipal principal) {
        return deleteRequestService.create(pageId, principal.getUser());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/approve")
    public WikiPageRs approve(@PathVariable Long id) {
        return deleteRequestService.approve(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/reject")
    public DeleteRequestRs reject(@PathVariable Long id) {
        return deleteRequestService.reject(id);
    }
}