package com.municipality.waste.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogEntry {
    private Long id;
    private String businessName; // null for platform-level events with no single business
    private String actorUsername;
    private String actorRole;
    private String action;
    private String description;
    private LocalDateTime createdAt;
}
