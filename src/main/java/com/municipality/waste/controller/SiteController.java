package com.municipality.waste.controller;

import com.municipality.waste.entity.Site;
import com.municipality.waste.service.SiteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sites")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
public class SiteController {

    private final SiteService siteService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SUPER_ADMIN')")
    public ResponseEntity<List<Site>> getAll() {
        return ResponseEntity.ok(siteService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SUPER_ADMIN')")
    public ResponseEntity<Site> getById(@PathVariable Long id) {
        return ResponseEntity.ok(siteService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Site> create(@Valid @RequestBody Site site) {
        return ResponseEntity.ok(siteService.create(site));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Site> update(@PathVariable Long id, @Valid @RequestBody Site site) {
        return ResponseEntity.ok(siteService.update(id, site));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        siteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
