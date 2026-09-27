package com.example.wiki.api.deleterequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.StreamSupport;

import com.example.wiki.entity.DeleteRequestEntity;

public record DeleteRequestRs(
        Long id,
        Long pageId,
        String pageTitle,
        String requestedBy,
        String status,
        LocalDateTime createdAt) {

    public static DeleteRequestRs from(DeleteRequestEntity entity) {
        return new DeleteRequestRs(
                entity.getId(),
                entity.getPage().getId(),
                entity.getPage().getTitle(),
                entity.getRequestedBy().getLogin(),
                entity.getStatus().name(),
                entity.getCreatedAt());
    }

    public static List<DeleteRequestRs> fromList(Iterable<DeleteRequestEntity> entities) {
        return StreamSupport.stream(entities.spliterator(), false)
                .map(DeleteRequestRs::from)
                .toList();
    }
}