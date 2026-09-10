package com.municipality.waste.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * A single recorded event for platform-level oversight — who did what, and
 * to which municipality. Deliberately snapshots the actor's username/role
 * as plain strings rather than a User FK: an audit trail needs to stay
 * readable and correct even if the account that performed the action is
 * later disabled or deleted.
 */
@Entity
@Table(name = "audit_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nullable: some events (e.g. a super admin action with no single
    // municipality involved) may not belong to one business.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "business_id")
    private Business business;

    @Column(name = "actor_username", nullable = false)
    private String actorUsername;

    @Column(name = "actor_role", nullable = false)
    private String actorRole;

    // Short machine-friendly code, e.g. LOGIN, STAFF_CREATED, BUSINESS_SUSPENDED.
    @Column(nullable = false)
    private String action;

    // Human-readable one-liner for display.
    @Column(nullable = false, length = 500)
    private String description;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
