package com.municipality.waste.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * The actual waste collected during a route visit to a site. Renamed from
 * WasteQuantity: this represents a real pickup event, not just a figure.
 */
@Entity
@Table(name = "waste_collections")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WasteCollection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal quantity;

    @Builder.Default
    private String unit = "TON"; // TON, KG

    @Column(name = "waste_type")
    private String wasteType; // GENERAL, PLASTIC, ORGANIC, RECYCLABLE, HAZARDOUS

    @Column(name = "collection_date", nullable = false)
    private LocalDateTime collectionDate;

    private String notes;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "site_id")
    private Site site;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "route_id")
    private CollectionRoute route;

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
