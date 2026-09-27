package com.example.wiki.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.wiki.api.tag.TagRq;
import com.example.wiki.api.tagrequest.TagRequestRq;
import com.example.wiki.api.tagrequest.TagRequestRs;
import com.example.wiki.entity.RequestStatus;
import com.example.wiki.entity.TagRequestEntity;
import com.example.wiki.entity.UserEntity;
import com.example.wiki.error.NotFoundException;
import com.example.wiki.repository.TagRequestRepository;

@Service
public class TagRequestService {

    private final TagRequestRepository repository;
    private final TagService tagService;

    public TagRequestService(TagRequestRepository repository, TagService tagService) {
        this.repository = repository;
        this.tagService = tagService;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public TagRequestEntity getEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(TagRequestEntity.class, id));
    }

    @Transactional(readOnly = true)
    public List<TagRequestRs> getAllPending() {
        return TagRequestRs.fromList(
                repository.findAllByStatusOrderByCreatedAtAsc(RequestStatus.PENDING));
    }

    @Transactional
    public TagRequestRs create(TagRequestRq dto, UserEntity user) {
        final TagRequestEntity entity = new TagRequestEntity(dto.name(), user);
        return TagRequestRs.from(repository.save(entity));
    }

    @Transactional
    public TagRequestRs approve(Long id) {
        final TagRequestEntity entity = getEntity(id);
        tagService.create(new TagRq(entity.getName()));
        entity.setStatus(RequestStatus.APPROVED);
        return TagRequestRs.from(repository.save(entity));
    }

    @Transactional
    public TagRequestRs reject(Long id) {
        final TagRequestEntity entity = getEntity(id);
        entity.setStatus(RequestStatus.REJECTED);
        return TagRequestRs.from(repository.save(entity));
    }
}