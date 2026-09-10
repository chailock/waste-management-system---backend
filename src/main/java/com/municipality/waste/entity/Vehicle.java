package com.municipality.waste.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "registration_number", nullable = false, unique = true)
    private String registrationNumber;

    private String make;

    private String model;

    // Tracked for internal capacity planning but intentionally not shown
    // on the Vehicles list/detail views — see frontend VehicleDetail notes.
    private BigDecimal capacity;

    @Builder.Default
    private String status = "ACTIVE"; // ACTIVE, MAINTENANCE, OUT_OF_SERVICE

    @Column(name = "license_expiry")
    private LocalDate licenseExpiry;

    @Column(name = "roadworthy_expiry")
    private LocalDate roadworthyExpiry;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
