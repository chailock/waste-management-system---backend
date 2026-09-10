package com.municipality.waste.controller;

import com.municipality.waste.dto.ActiveStatusRequest;
import com.municipality.waste.dto.AuthResponse;
import com.municipality.waste.dto.BusinessSummary;
import com.municipality.waste.dto.RegisterRequest;
import com.municipality.waste.service.AuthService;
import com.municipality.waste.service.BusinessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** SUPER_ADMIN's "all municipalities" dashboard. */
@RestController
@RequestMapping("/businesses")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class BusinessController {

    private final BusinessService businessService;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<List<BusinessSummary>> getAll() {
        return ResponseEntity.ok(businessService.findAll());
    }

    /** Side-effect-free — for the sidebar's polling badge, summed across every municipality. */
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount() {
        return ResponseEntity.ok(businessService.totalUnreadMessages());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusinessSummary> getById(@PathVariable Long id) {
        return ResponseEntity.ok(businessService.findById(id));
    }

    /** Suspend or reactivate a municipality — blocks/restores login for its staff. */
    @PatchMapping("/{id}/active")
    public ResponseEntity<BusinessSummary> setActive(@PathVariable Long id, @RequestBody ActiveStatusRequest request) {
        return ResponseEntity.ok(businessService.setActive(id, request.isActive()));
    }

    /** Recovery path: create the first staff account for a municipality that currently has none. */
    @PostMapping("/{id}/staff")
    public ResponseEntity<AuthResponse> addFirstStaff(@PathVariable Long id, @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.registerStaffForBusiness(id, request));
    }
}
