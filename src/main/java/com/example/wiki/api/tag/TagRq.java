package com.example.wiki.api.tag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TagRq(
        @NotBlank @Size(max = 50) String name) {
}