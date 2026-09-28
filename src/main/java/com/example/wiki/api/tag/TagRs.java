package com.example.wiki.api.tag;

import java.util.List;
import java.util.stream.StreamSupport;

import com.example.wiki.entity.TagEntity;

public record TagRs(Long id, String name) {

    public static TagRs from(TagEntity entity) {
        return new TagRs(entity.getId(), entity.getName());
    }

    public static List<TagRs> fromList(Iterable<TagEntity> entities) {
        return StreamSupport.stream(entities.spliterator(), false)
                .map(TagRs::from)
                .toList();
    }
}