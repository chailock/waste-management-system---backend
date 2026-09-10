package com.municipality.waste.repository;

import com.municipality.waste.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    // Capped rather than a plain findAll() — a platform audit trail grows
    // forever, and the oversight view only ever needs recent activity.
    List<AuditLog> findTop200ByOrderByCreatedAtDesc();
    List<AuditLog> findTop200ByBusinessIdOrderByCreatedAtDesc(Long businessId);
}
