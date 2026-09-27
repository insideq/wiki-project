package com.example.wiki.api.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserLoginRq(
        @NotBlank @Size(min = 3) String login,
        @NotBlank @Size(min = 3) String password) {
}