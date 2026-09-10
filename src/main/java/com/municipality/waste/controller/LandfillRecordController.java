package com.municipality.waste.controller;

import com.municipality.waste.entity.LandfillRecord;
import com.municipality.waste.service.LandfillRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/landfills")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
public class LandfillRecordController {

    private final LandfillRecordService landfillRecordService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SUPER_ADMIN')")
    public ResponseEntity<List<LandfillRecord>> getAll() {
        return ResponseEntity.ok(landfillRecordService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SUPER_ADMIN')")
    public ResponseEntity<LandfillRecord> getById(@PathVariable Long id) {
        return ResponseEntity.ok(landfillRecordService.findById(id));
    }

    @PostMapping
    public ResponseEntity<LandfillRecord> create(@Valid @RequestBody LandfillRecord record) {
        return ResponseEntity.ok(landfillRecordService.create(record));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LandfillRecord> update(@PathVariable Long id, @Valid @RequestBody LandfillRecord record) {
        return ResponseEntity.ok(landfillRecordService.update(id, record));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        landfillRecordService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
