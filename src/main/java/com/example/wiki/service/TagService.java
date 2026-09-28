package com.example.wiki.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.wiki.api.tag.TagRq;
import com.example.wiki.api.tag.TagRs;
import com.example.wiki.entity.TagEntity;
import com.example.wiki.error.AlreadyExistsException;
import com.example.wiki.error.NotFoundException;
import com.example.wiki.repository.TagRepository;

@Service
public class TagService {

    private final TagRepository repository;

    public TagService(TagRepository repository) {
        this.repository = repository;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public TagEntity getEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(TagEntity.class, id));
    }

    @Transactional(readOnly = true)
    public List<TagRs> getAll() {
        return TagRs.fromList(repository.findAll());
    }

    @Transactional(readOnly = true)
    public TagRs get(Long id) {
        return TagRs.from(getEntity(id));
    }

    @Transactional
    public TagRs create(TagRq dto) {
        repository.findOneByNameIgnoreCase(dto.name()).ifPresent(t -> {
            throw new AlreadyExistsException(TagEntity.class, dto.name());
        });
        final TagEntity entity = new TagEntity(dto.name());
        return TagRs.from(repository.save(entity));
    }

    @Transactional
    public TagRs delete(Long id) {
        final TagEntity entity = getEntity(id);
        repository.delete(entity);
        return TagRs.from(entity);
    }
}