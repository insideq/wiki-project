package com.example.wiki.api;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PageHelper {
    private PageHelper() {
    }

    public static Pageable toPageable(int page, int size) {
        return PageRequest.of(page - 1, size);
    }
}