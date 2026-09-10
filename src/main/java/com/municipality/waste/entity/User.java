package com.municipality.waste.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @NotBlank
    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Builder.Default
    @Column(nullable = false)
    private boolean enabled = true;

    // Whether this user wants an email when a new message arrives on their
    // side of the platform<->municipality thread. Defaults to on; users can
    // opt out from their account settings without affecting in-app badges.
    @Builder.Default
    @Column(name = "email_notifications_enabled", nullable = false)
    private boolean emailNotificationsEnabled = true;

    // Nullable: never set until the first successful login. Lets a super
    // admin tell an account that's actually being used apart from one
    // that's just been created and forgotten.
    @Column(name = "last_login_at")
    private java.time.LocalDateTime lastLoginAt;

    // Every user belongs to exactly one Business (tenant). Admins manage
    // users and data scoped to their own business only.
    // Nullable: SUPER_ADMIN accounts are platform-level and belong to no
    // single municipality (see DataSeeder.seedSuperAdmin()). Every other
    // role (ADMIN/MANAGER) is created with a business and this is always
    // set for them.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "business_id", nullable = true)
    private Business business;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
