package com.municipality.waste.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStats {
    private long totalSites;
    private long totalVehicles;
    private long activeRoutes;
    private long openIncidents;
    private long activeContracts;
    private double totalCollectedTonsThisMonth;
    private double totalDisposedTonsThisMonth;
    private long expiringComplianceDocuments;
    // Every ADMIN/MANAGER assigned to this municipality — lets a super
    // admin see at a glance who's actually responsible for it.
    private List<StaffMember> staff;
}
