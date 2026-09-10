package com.municipality.waste.controller;

import com.municipality.waste.entity.ComplianceDocument;
import com.municipality.waste.service.ComplianceDocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/compliance-documents")
@RequiredArgsConstructor
public class ComplianceDocumentController {

    private final ComplianceDocumentService complianceDocumentService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SUPER_ADMIN')")
    public ResponseEntity<List<ComplianceDocument>> getAll() {
        return ResponseEntity.ok(complianceDocumentService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SUPER_ADMIN')")
    public ResponseEntity<ComplianceDocument> getById(@PathVariable Long id) {
        return ResponseEntity.ok(complianceDocumentService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ComplianceDocument> create(@Valid @RequestBody ComplianceDocument doc) {
        return ResponseEntity.ok(complianceDocumentService.create(doc));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ComplianceDocument> update(@PathVariable Long id, @Valid @RequestBody ComplianceDocument doc) {
        return ResponseEntity.ok(complianceDocumentService.update(id, doc));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        complianceDocumentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
