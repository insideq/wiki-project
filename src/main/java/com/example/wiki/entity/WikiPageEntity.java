package com.example.wiki.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "wiki_pages")
public class WikiPageEntity extends BaseEntity {

    @Column(length = 200, nullable = false)
    private String title;

    @Column(columnDefinition = "CLOB", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private UserEntity author;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private WikiPageVisibility visibility = WikiPageVisibility.PUBLIC;

    @Enumerated(EnumType.STRING)
    @Column(name = "edit_policy", length = 20, nullable = false)
    private WikiPageEditPolicy editPolicy = WikiPageEditPolicy.OPEN;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private WikiPageStatus status = WikiPageStatus.ACTIVE;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "wiki_page_tags", joinColumns = @JoinColumn(name = "page_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    @OrderBy("name ASC")
    private Set<TagEntity> tags = new HashSet<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public WikiPageEntity() {
    }

    public WikiPageEntity(
            String title,
            String content,
            UserEntity author,
            WikiPageVisibility visibility,
            WikiPageEditPolicy editPolicy) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.visibility = visibility;
        this.editPolicy = editPolicy;
        this.status = WikiPageStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UserEntity getAuthor() {
        return author;
    }

    public void setAuthor(UserEntity author) {
        this.author = author;
    }

    public WikiPageVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(WikiPageVisibility visibility) {
        this.visibility = visibility;
    }

    public WikiPageEditPolicy getEditPolicy() {
        return editPolicy;
    }

    public void setEditPolicy(WikiPageEditPolicy editPolicy) {
        this.editPolicy = editPolicy;
    }

    public WikiPageStatus getStatus() {
        return status;
    }

    public void setStatus(WikiPageStatus status) {
        this.status = status;
    }

    public Set<TagEntity> getTags() {
        return tags;
    }

    public void setTags(Set<TagEntity> tags) {
        this.tags = tags;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}