package com.example.wiki.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "delete_requests")
public class DeleteRequestEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "page_id", nullable = false)
    private WikiPageEntity page;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requested_by", nullable = false)
    private UserEntity requestedBy;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private RequestStatus status = RequestStatus.PENDING;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public DeleteRequestEntity() {
    }

    public DeleteRequestEntity(WikiPageEntity page, UserEntity requestedBy) {
        this.page = page;
        this.requestedBy = requestedBy;
        this.status = RequestStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public WikiPageEntity getPage() {
        return page;
    }

    public void setPage(WikiPageEntity page) {
        this.page = page;
    }

    public UserEntity getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(UserEntity requestedBy) {
        this.requestedBy = requestedBy;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}