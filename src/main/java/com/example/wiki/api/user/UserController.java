package com.example.wiki.api.user;

import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.example.wiki.service.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Profile("front")
@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping(Constants.API_URL + UserController.URL)
public class UserController {
    public static final String URL = "/user";
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public PageRs<UserRs> getAll(
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size) {
        return userService.getAll(PageHelper.toPageable(page, size));
    }

    @PreAuthorize("hasRole('ADMIN') or #login == authentication.principal.username")
    @GetMapping("/{login}")
    public UserRs get(@PathVariable @NotBlank String login) {
        return userService.get(login);
    }

    @PostMapping
    public UserRs create(@RequestBody @Valid UserRq dto) {
        return userService.create(dto);
    }

    @PreAuthorize("hasRole('ADMIN') or #login == authentication.principal.username")
    @PutMapping("/{login}")
    public UserRs update(@PathVariable @NotBlank String login, @RequestBody @Valid UserUpdateRq dto) {
        return userService.update(login, dto);
    }

    @DeleteMapping("/{login}")
    public UserRs delete(@PathVariable @NotBlank String login, @RequestParam @NotBlank String password) {
        return userService.delete(login, password);
    }
}
