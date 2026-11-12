package com.example.wiki.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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
import com.example.wiki.repository.DeleteRequestRepository;
import com.example.wiki.repository.WikiPageRepository;

@Service
public class WikiPageService {

    private final WikiPageRepository repository;
    private final TagService tagService;
    private final DeleteRequestRepository deleteRequestRepository;

    public WikiPageService(WikiPageRepository repository, TagService tagService,
            DeleteRequestRepository deleteRequestRepository) {
        this.repository = repository;
        this.tagService = tagService;
        this.deleteRequestRepository = deleteRequestRepository;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public WikiPageEntity getEntity(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(WikiPageEntity.class, id));
    }

    @Transactional(readOnly = true)
    public PageRs<WikiPageRs> search(String query, List<Long> tagIds, UserEntity currentUser, Pageable pageable) {
        final Long userId = currentUser != null ? currentUser.getId() : null;
        // Если список пустой, передаем null, чтобы запрос его проигнорировал
        final List<Long> filterTags = (tagIds == null || tagIds.isEmpty()) ? null : tagIds;
        final Page<WikiPageEntity> page = repository.searchActive(query, filterTags, userId, pageable);
        return PageRs.from(page, WikiPageRs::shortFrom);
    }

    @Transactional(readOnly = true)
    public WikiPageRs get(Long id, UserEntity currentUser) {
        final WikiPageEntity entity = getEntity(id);

        // Если статья приватная, проверяем только то, что пользователь авторизован
        if (entity.getVisibility() == WikiPageVisibility.PRIVATE) {
            if (currentUser == null) {
                throw new AccessDeniedException("Private page. Authorization required.");
            }
        }
        return WikiPageRs.full(entity);
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

        if (currentUser == null) {
            throw new AccessDeniedException("User is not authenticated.");
        }

        // Проверка политики редактирования (OWNER_ONLY)
        if (entity.getEditPolicy() == WikiPageEditPolicy.OWNER_ONLY) {
            boolean isAuthor = Objects.equals(entity.getAuthor().getId(), currentUser.getId());
            boolean isAdmin = currentUser.getRole() == UserRole.ADMIN;
            if (!isAuthor && !isAdmin) {
                throw new AccessDeniedException("Редактирование разрешено только автору статьи.");
            }
        }
        // Если editPolicy == OPEN, то любой авторизованный проходит дальше.

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
        deleteRequestRepository.deleteAllByPageId(id);
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