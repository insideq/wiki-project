package com.example.wiki.api.wiki;

import java.util.Set;

import com.example.wiki.entity.WikiPageEditPolicy;
import com.example.wiki.entity.WikiPageVisibility;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record WikiPageRq(
        @NotBlank @Size(max = 200) String title,
        @NotBlank String content,
        @NotNull WikiPageVisibility visibility,
        @NotNull WikiPageEditPolicy editPolicy,
        Set<Long> tagIds) {
}