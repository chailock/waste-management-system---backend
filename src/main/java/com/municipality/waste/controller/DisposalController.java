package com.municipality.waste.controller;

import com.municipality.waste.entity.Disposal;
import com.municipality.waste.service.DisposalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/disposals")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
public class DisposalController {

    private final DisposalService disposalService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SUPER_ADMIN')")
    public ResponseEntity<List<Disposal>> getAll() {
        return ResponseEntity.ok(disposalService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SUPER_ADMIN')")
    public ResponseEntity<Disposal> getById(@PathVariable Long id) {
        return ResponseEntity.ok(disposalService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Disposal> create(@Valid @RequestBody Disposal disposal) {
        return ResponseEntity.ok(disposalService.create(disposal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Disposal> update(@PathVariable Long id, @Valid @RequestBody Disposal disposal) {
        return ResponseEntity.ok(disposalService.update(id, disposal));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        disposalService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
