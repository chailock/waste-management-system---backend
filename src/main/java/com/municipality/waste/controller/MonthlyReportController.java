package com.municipality.waste.controller;

import com.municipality.waste.entity.MonthlyReport;
import com.municipality.waste.security.UserPrincipal;
import com.municipality.waste.service.MonthlyReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
public class MonthlyReportController {

    private final MonthlyReportService monthlyReportService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SUPER_ADMIN')")
    public ResponseEntity<List<MonthlyReport>> getAll() {
        return ResponseEntity.ok(monthlyReportService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SUPER_ADMIN')")
    public ResponseEntity<MonthlyReport> getById(@PathVariable Long id) {
        return ResponseEntity.ok(monthlyReportService.findById(id));
    }

    @PostMapping("/generate")
    public ResponseEntity<MonthlyReport> generate(@RequestParam Integer month,
                                                    @RequestParam Integer year,
                                                    @RequestParam(required = false) String notes,
                                                    @AuthenticationPrincipal UserPrincipal principal) {
        String generatedBy = principal != null ? principal.getUsername() : "system";
        return ResponseEntity.ok(monthlyReportService.generate(month, year, generatedBy, notes));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        monthlyReportService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
