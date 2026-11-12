package com.example.wiki.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.wiki.entity.WikiPageEntity;

public interface WikiPageRepository extends JpaRepository<WikiPageEntity, Long> {

    // 1. Запрос для H2 (разработка) - обычный LIKE
    @Query("select distinct p from WikiPageEntity p " +
            "left join p.tags t " +
            "where p.status = com.example.wiki.entity.WikiPageStatus.ACTIVE " +
            "and (p.visibility = com.example.wiki.entity.WikiPageVisibility.PUBLIC " +
            "     or (:userId is not null)) " +
            "and (:query is null or lower(p.title) like lower(concat('%', :query, '%')) " +
            "     or lower(p.content) like lower(concat('%', :query, '%'))) " +
            "and (:tagIds is null or t.id in :tagIds) " +
            "order by p.updatedAt desc")
    Page<WikiPageEntity> searchActiveH2(
            @Param("query") String query,
            @Param("tagIds") List<Long> tagIds,
            @Param("userId") Long userId,
            Pageable pageable);

    // 2. Запрос для PostgreSQL (продакшен) - Полнотекстовый поиск (FTS)
    @Query(value = "SELECT DISTINCT p.* FROM wiki_pages p " +
            "LEFT JOIN wiki_page_tags wpt ON p.id = wpt.page_id " +
            "WHERE p.status = 'ACTIVE' " +
            "AND (p.visibility = 'PUBLIC' OR CAST(:userId AS BIGINT) IS NOT NULL) " +
            "AND (:query IS NULL OR p.search_vector @@ plainto_tsquery('russian', :query)) " +
            "AND (CAST(:tagIds AS BIGINT[]) IS NULL OR wpt.tag_id = ANY(CAST(:tagIds AS BIGINT[]))) " +
            "ORDER BY p.updated_at DESC", // <--- ДОБАВИТЬ (именно p.updated_at!)
            countQuery = "SELECT count(DISTINCT p.id) FROM wiki_pages p " +
                    "LEFT JOIN wiki_page_tags wpt ON p.id = wpt.page_id " +
                    "WHERE p.status = 'ACTIVE' " +
                    "AND (p.visibility = 'PUBLIC' OR CAST(:userId AS BIGINT) IS NOT NULL) " +
                    "AND (:query IS NULL OR p.search_vector @@ plainto_tsquery('russian', :query)) " +
                    "AND (CAST(:tagIds AS BIGINT[]) IS NULL OR wpt.tag_id = ANY(CAST(:tagIds AS BIGINT[])))", nativeQuery = true)
    Page<WikiPageEntity> searchActivePostgres(
            @Param("query") String query,
            @Param("tagIds") List<Long> tagIds,
            @Param("userId") Long userId,
            Pageable pageable);
}