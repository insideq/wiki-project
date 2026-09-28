package com.example.wiki.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.wiki.entity.DeleteRequestEntity;
import com.example.wiki.entity.RequestStatus;

public interface DeleteRequestRepository extends JpaRepository<DeleteRequestEntity, Long> {
    List<DeleteRequestEntity> findAllByStatusOrderByCreatedAtAsc(RequestStatus status);
}