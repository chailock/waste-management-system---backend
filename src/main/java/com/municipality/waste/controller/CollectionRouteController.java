package com.municipality.waste.controller;

import com.municipality.waste.entity.CollectionRoute;
import com.municipality.waste.service.CollectionRouteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/routes")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
public class CollectionRouteController {

    private final CollectionRouteService routeService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SUPER_ADMIN')")
    public ResponseEntity<List<CollectionRoute>> getAll() {
        return ResponseEntity.ok(routeService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SUPER_ADMIN')")
    public ResponseEntity<CollectionRoute> getById(@PathVariable Long id) {
        return ResponseEntity.ok(routeService.findById(id));
    }

    @PostMapping
    public ResponseEntity<CollectionRoute> create(@Valid @RequestBody CollectionRoute route) {
        return ResponseEntity.ok(routeService.create(route));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CollectionRoute> update(@PathVariable Long id, @Valid @RequestBody CollectionRoute route) {
        return ResponseEntity.ok(routeService.update(id, route));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        routeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
