package com.municipality.waste.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** One card/row on the super-admin's "all municipalities" dashboard. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessSummary {
    private Long id;
    private String name;
    private String contactEmail;
    private String contactPhone;
    private String address;
    private boolean active;
    private LocalDateTime createdAt;

    private long siteCount;
    private long vehicleCount;
    private long activeRouteCount;
    private long openIncidentCount;
    private long activeContractCount;
    private double totalCollectedTonsThisMonth;
    private long unreadMessageCount;
}
