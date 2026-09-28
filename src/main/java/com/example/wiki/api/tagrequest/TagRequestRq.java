package com.example.wiki.api.tagrequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TagRequestRq(
        @NotBlank @Size(max = 50) String name) {
}