package com.example.wiki.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.wiki.entity.DeleteRequestEntity;
import com.example.wiki.entity.RequestStatus;

public interface DeleteRequestRepository extends JpaRepository<DeleteRequestEntity, Long> {
    List<DeleteRequestEntity> findAllByStatusOrderByCreatedAtAsc(RequestStatus status);

    @Modifying
    @Query("delete from DeleteRequestEntity d where d.page.id = :pageId")
    void deleteAllByPageId(@Param("pageId") Long pageId);
}