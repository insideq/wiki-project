package com.example.wiki.api;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.data.domain.Page;

public record PageRs<D>(
        List<D> items,
        int itemsCount,
        int currentPage,
        int currentSize,
        int totalPages,
        long totalItems,
        boolean isFirst,
        boolean isLast,
        boolean hasNext,
        boolean hasPrevious) {

    public List<D> items() {
        return Optional.ofNullable(items).orElse(Collections.emptyList());
    }

    public static <D, E> PageRs<D> from(Page<E> page, Function<E, D> mapper) {
        return new PageRs<>(
                page.getContent().stream().map(mapper).toList(),
                page.getNumberOfElements(),
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.isFirst(),
                page.isLast(),
                page.hasNext(),
                page.hasPrevious());
    }
}
