package com.example.wiki.api.user;

import com.example.wiki.validation.password.PasswordMatch;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@PasswordMatch(first = "password", second = "passwordConfirm")
public record UserRq(
        @NotBlank @Size(min = 3) String login,
        @NotBlank @Size(min = 3) String password,
        @NotBlank @Size(min = 3) String passwordConfirm) {
}