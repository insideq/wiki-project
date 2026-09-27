package com.example.wiki.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.wiki.entity.TagEntity;

public interface TagRepository extends JpaRepository<TagEntity, Long> {
    Optional<TagEntity> findOneByNameIgnoreCase(String name);
}