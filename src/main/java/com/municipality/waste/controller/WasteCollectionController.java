package com.municipality.waste.controller;

import com.municipality.waste.entity.WasteCollection;
import com.municipality.waste.service.WasteCollectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/collections")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
public class WasteCollectionController {

    private final WasteCollectionService wasteCollectionService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SUPER_ADMIN')")
    public ResponseEntity<List<WasteCollection>> getAll() {
        return ResponseEntity.ok(wasteCollectionService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SUPER_ADMIN')")
    public ResponseEntity<WasteCollection> getById(@PathVariable Long id) {
        return ResponseEntity.ok(wasteCollectionService.findById(id));
    }

    @PostMapping
    public ResponseEntity<WasteCollection> create(@Valid @RequestBody WasteCollection entry) {
        return ResponseEntity.ok(wasteCollectionService.create(entry));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WasteCollection> update(@PathVariable Long id, @Valid @RequestBody WasteCollection entry) {
        return ResponseEntity.ok(wasteCollectionService.update(id, entry));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        wasteCollectionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
