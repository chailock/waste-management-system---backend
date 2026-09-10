package com.municipality.waste.controller;

import com.municipality.waste.dto.AuditLogEntry;
import com.municipality.waste.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Platform-wide activity trail — SUPER_ADMIN only. */
@RestController
@RequestMapping("/audit-logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class AuditLogController {

    private final AuditService auditService;

    @GetMapping
    public ResponseEntity<List<AuditLogEntry>> recent(@RequestParam(required = false) Long businessId) {
        if (businessId != null) {
            return ResponseEntity.ok(auditService.recentForBusiness(businessId));
        }
        return ResponseEntity.ok(auditService.recent());
    }
}
