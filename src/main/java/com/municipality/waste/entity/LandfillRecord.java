package com.municipality.waste.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * The official weighbridge/ticket record proving a load reached a landfill.
 * The ticket number is the field of record for audits and compliance —
 * quantity ("weight") is displayed alongside it as one combined line on
 * the frontend (e.g. "VL-2026-000182 — 2.4 tons") rather than as two
 * separate rows.
 */
@Entity
@Table(name = "landfill_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LandfillRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "landfill_name", nullable = false)
    private String landfillName;

    @Column(name = "landfill_location")
    private String landfillLocation;

    @Column(name = "weighbridge_number")
    private String weighbridgeNumber;

    @Column(precision = 12, scale = 3)
    private BigDecimal quantity;

    @Column(name = "waste_type")
    private String wasteType;

    @Column(name = "disposal_date", nullable = false)
    private LocalDate disposalDate;

    @Column(name = "ticket_number", unique = true)
    private String ticketNumber;

    private String notes;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "disposal_id")
    private Disposal disposal;

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
