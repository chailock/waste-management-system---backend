package com.municipality.waste.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * A message in the communication thread between the platform (SUPER_ADMIN)
 * and one municipality (Business). Either side can post into the thread.
 * Each side tracks its own read state independently: "readBySuperAdmin" is
 * whether the platform admin has seen it, "readByBusiness" is whether the
 * municipality's own staff have. A message is automatically "read" on the
 * side that authored it — you don't need a notification about your own
 * message.
 */
@Entity
@Table(name = "messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String body;

    @ManyToOne(fetch = FetchType.EAGER)
    private User sender;

    @ManyToOne(fetch = FetchType.EAGER)
    private Business business;

    @Builder.Default
    @Column(name = "read_by_super_admin", nullable = false)
    private boolean readBySuperAdmin = false;

    @Builder.Default
    @Column(name = "read_by_business", nullable = false)
    private boolean readByBusiness = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
