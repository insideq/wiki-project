package com.example.wiki.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tags")
public class TagEntity extends BaseEntity {

    @Column(length = 50, nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "tags")
    private Set<WikiPageEntity> pages = new HashSet<>();

    public TagEntity() {
    }

    public TagEntity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<WikiPageEntity> getPages() {
        return pages;
    }
}