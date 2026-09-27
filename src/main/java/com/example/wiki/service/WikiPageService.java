package com.example.wiki.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.wiki.api.PageRs;
import com.example.wiki.api.wiki.WikiPageRq;
import com.example.wiki.api.wiki.WikiPageRs;
import com.example.wiki.entity.TagEntity;
import com.example.wiki.entity.UserEntity;
import com.example.wiki.entity.UserRole;
import com.example.wiki.entity.WikiPageEditPolicy;
import com.example.wiki.entity.WikiPageEntity;
import com.example.wiki.entity.WikiPageVisibility;
import com.example.wiki.error.NotFoundException;
import com.example.wiki.repository.WikiPageRepository;

@Service
public class WikiPageService {

    private final WikiPageRepository repository;
    private final TagService tagService;

    public WikiPageService(WikiPageRepository repository, TagService tagService) {
        this.repository = repository;
        this.tagService = tagService;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public WikiPageEntity getEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(WikiPageEntity.class, id));
    }

    @Transactional(readOnly = true)
    public PageRs<WikiPageRs> search(String query, Long tagId, Pageable pageable) {
        final Page<WikiPageEntity> page = repository.searchActive(query, tagId, pageable);
        return PageRs.from(page, WikiPageRs::shortFrom);
    }

    @Transactional(readOnly = true)
    public WikiPageRs get(Long id) {
        return WikiPageRs.full(getEntity(id));
    }

    @Transactional
    public WikiPageRs create(WikiPageRq dto, UserEntity author) {
        final WikiPageEntity entity = new WikiPageEntity(
                dto.title(),
                dto.content(),
                author,
                dto.visibility(),
                dto.editPolicy());
        applyTags(entity, dto.tagIds());
        return WikiPageRs.full(repository.save(entity));
    }

    @Transactional
    public WikiPageRs update(Long id, WikiPageRq dto, UserEntity currentUser) {
        final WikiPageEntity entity = getEntity(id);

        // Проверка: если приватная — только автор или админ
        if (entity.getVisibility() == WikiPageVisibility.PRIVATE
                && !entity.getAuthor().getId().equals(currentUser.getId())
                && currentUser.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Private page. Access denied.");
        }

        // Проверка: если OWNER_ONLY — только автор или админ
        if (entity.getEditPolicy() == WikiPageEditPolicy.OWNER_ONLY
                && !entity.getAuthor().getId().equals(currentUser.getId())
                && currentUser.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Edit forbidden. Owner only.");
        }

        entity.setTitle(dto.title());
        entity.setContent(dto.content());
        entity.setVisibility(dto.visibility());
        entity.setEditPolicy(dto.editPolicy());
        entity.getTags().clear();
        applyTags(entity, dto.tagIds());
        entity.setUpdatedAt(LocalDateTime.now());
        return WikiPageRs.full(repository.save(entity));
    }

    @Transactional
    public WikiPageRs delete(Long id) {
        final WikiPageEntity entity = getEntity(id);
        repository.delete(entity);
        return WikiPageRs.full(entity);
    }

    private void applyTags(WikiPageEntity entity, Set<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }
        final Set<TagEntity> tags = new HashSet<>();
        for (Long tagId : tagIds) {
            tags.add(tagService.getEntity(tagId));
        }
        entity.setTags(tags);
    }
}