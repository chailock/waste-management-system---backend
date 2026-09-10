package com.municipality.waste.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Records what happened to a specific WasteCollection after pickup.
 * Quantity is intentionally not shown as its own column on the Disposal
 * list view — it is inherited from the linked collection to avoid a
 * duplicate, potentially conflicting figure. It remains stored here so a
 * disposal can be partial or independently corrected if needed.
 */
@Entity
@Table(name = "disposals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Disposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal quantity;

    @Column(name = "waste_type")
    private String wasteType;

    @Column(name = "disposal_date", nullable = false)
    private LocalDateTime disposalDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DisposalMethod method;

    private String notes;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "collection_id")
    private WasteCollection collection;

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
