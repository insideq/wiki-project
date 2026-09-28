package com.example.wiki.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.wiki.api.deleterequest.DeleteRequestRs;
import com.example.wiki.api.wiki.WikiPageRs;
import com.example.wiki.entity.DeleteRequestEntity;
import com.example.wiki.entity.RequestStatus;
import com.example.wiki.entity.UserEntity;
import com.example.wiki.entity.WikiPageEntity;
import com.example.wiki.entity.WikiPageStatus;
import com.example.wiki.error.NotFoundException;
import com.example.wiki.repository.DeleteRequestRepository;

@Service
public class DeleteRequestService {

    private final DeleteRequestRepository repository;
    private final WikiPageService wikiPageService;

    public DeleteRequestService(DeleteRequestRepository repository, WikiPageService wikiPageService) {
        this.repository = repository;
        this.wikiPageService = wikiPageService;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public DeleteRequestEntity getEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(DeleteRequestEntity.class, id));
    }

    @Transactional(readOnly = true)
    public List<DeleteRequestRs> getAllPending() {
        return DeleteRequestRs.fromList(
                repository.findAllByStatusOrderByCreatedAtAsc(RequestStatus.PENDING));
    }

    @Transactional
    public DeleteRequestRs create(Long pageId, UserEntity user) {
        final WikiPageEntity page = wikiPageService.getEntity(pageId);
        page.setStatus(WikiPageStatus.PENDING_DELETION);
        final DeleteRequestEntity entity = new DeleteRequestEntity(page, user);
        return DeleteRequestRs.from(repository.save(entity));
    }

    @Transactional
    public WikiPageRs approve(Long id) {
        final DeleteRequestEntity entity = getEntity(id);
        final WikiPageEntity page = entity.getPage();
        entity.setStatus(RequestStatus.APPROVED);
        repository.save(entity);
        return wikiPageService.delete(page.getId());
    }

    @Transactional
    public DeleteRequestRs reject(Long id) {
        final DeleteRequestEntity entity = getEntity(id);
        entity.getPage().setStatus(WikiPageStatus.ACTIVE);
        entity.setStatus(RequestStatus.REJECTED);
        return DeleteRequestRs.from(repository.save(entity));
    }
}