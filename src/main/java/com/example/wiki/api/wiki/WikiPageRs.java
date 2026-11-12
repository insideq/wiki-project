package com.example.wiki.api.wiki;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.StreamSupport;

import com.example.wiki.api.tag.TagRs;
import com.example.wiki.entity.WikiPageEntity;

public record WikiPageRs(
        Long id,
        String title,
        String content,
        String authorLogin,
        String visibility,
        String editPolicy,
        String status,
        List<TagRs> tags,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static WikiPageRs full(WikiPageEntity entity) {
        return new WikiPageRs(
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getAuthor().getLogin(),
                entity.getVisibility().name(),
                entity.getEditPolicy().name(),
                entity.getStatus().name(),
                TagRs.fromList(entity.getTags()),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public static WikiPageRs shortFrom(WikiPageEntity entity) {
        String shortContent = null;
        if (entity.getContent() != null) {
            // Убираем markdown-разметку для картинок
            shortContent = entity.getContent().replaceAll("!\\[[^\\]]*\\]\\([^\\)]*\\)", "[Изображение]");

            // Убираем Markdown-символы (жирность, курсив, заголовки, зачеркивания, цитаты)
            shortContent = shortContent.replaceAll("[*#~`>_-]", "");

            // Убираем множественные пробелы и переносы
            shortContent = shortContent.replaceAll("\\s+", " ").trim();

            // Оставляем первые 150 символов
            if (shortContent.length() > 150) {
                shortContent = shortContent.substring(0, 150) + "...";
            }
        }
        return new WikiPageRs(
                entity.getId(),
                entity.getTitle(),
                shortContent,
                entity.getAuthor().getLogin(),
                entity.getVisibility().name(),
                entity.getEditPolicy().name(),
                entity.getStatus().name(),
                TagRs.fromList(entity.getTags()),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public static List<WikiPageRs> fromList(Iterable<WikiPageEntity> entities) {
        return StreamSupport.stream(entities.spliterator(), false)
                .map(WikiPageRs::shortFrom)
                .toList();
    }
}