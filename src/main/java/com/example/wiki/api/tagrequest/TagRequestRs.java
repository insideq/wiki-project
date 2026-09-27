package com.example.wiki.api.tagrequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.StreamSupport;

import com.example.wiki.entity.TagRequestEntity;

public record TagRequestRs(
        Long id,
        String name,
        String requestedBy,
        String status,
        LocalDateTime createdAt) {

    public static TagRequestRs from(TagRequestEntity entity) {
        return new TagRequestRs(
                entity.getId(),
                entity.getName(),
                entity.getRequestedBy().getLogin(),
                entity.getStatus().name(),
                entity.getCreatedAt());
    }

    public static List<TagRequestRs> fromList(Iterable<TagRequestEntity> entities) {
        return StreamSupport.stream(entities.spliterator(), false)
                .map(TagRequestRs::from)
                .toList();
    }
}