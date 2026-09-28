package com.example.wiki.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.wiki.entity.RequestStatus;
import com.example.wiki.entity.TagRequestEntity;

public interface TagRequestRepository extends JpaRepository<TagRequestEntity, Long> {
    List<TagRequestEntity> findAllByStatusOrderByCreatedAtAsc(RequestStatus status);
}