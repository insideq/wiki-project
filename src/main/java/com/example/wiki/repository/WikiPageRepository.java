package com.example.wiki.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.wiki.entity.WikiPageEntity;

public interface WikiPageRepository extends JpaRepository<WikiPageEntity, Long> {

    @Query("select distinct p from WikiPageEntity p " +
            "left join p.tags t " +
            "where p.status = com.example.wiki.entity.WikiPageStatus.ACTIVE " +
            "and (:query is null or lower(p.title) like lower(concat('%', :query, '%')) " +
            "     or lower(p.content) like lower(concat('%', :query, '%'))) " +
            "and (:tagId is null or t.id = :tagId)")
    Page<WikiPageEntity> searchActive(
            @Param("query") String query,
            @Param("tagId") Long tagId,
            Pageable pageable);
}