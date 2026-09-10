package com.municipality.waste.service;

import com.municipality.waste.dto.AuditLogEntry;
import com.municipality.waste.entity.AuditLog;
import com.municipality.waste.entity.Business;
import com.municipality.waste.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    /** Fire-and-forget: never let an audit-log write break the action it's recording. */
    public void record(Business business, String actorUsername, String actorRole, String action, String description) {
        try {
            AuditLog entry = AuditLog.builder()
                    .business(business)
                    .actorUsername(actorUsername)
                    .actorRole(actorRole)
                    .action(action)
                    .description(description)
                    .build();
            auditLogRepository.save(entry);
        } catch (Exception e) {
            // Deliberately swallowed: an audit-log write failure must never
            // surface as a failure of the real operation it's recording.
        }
    }

    public List<AuditLogEntry> recent() {
        return auditLogRepository.findTop200ByOrderByCreatedAtDesc().stream()
                .map(this::toEntry)
                .toList();
    }

    public List<AuditLogEntry> recentForBusiness(Long businessId) {
        return auditLogRepository.findTop200ByBusinessIdOrderByCreatedAtDesc(businessId).stream()
                .map(this::toEntry)
                .toList();
    }

    private AuditLogEntry toEntry(AuditLog log) {
        return AuditLogEntry.builder()
                .id(log.getId())
                .businessName(log.getBusiness() != null ? log.getBusiness().getName() : null)
                .actorUsername(log.getActorUsername())
                .actorRole(log.getActorRole())
                .action(log.getAction())
                .description(log.getDescription())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
