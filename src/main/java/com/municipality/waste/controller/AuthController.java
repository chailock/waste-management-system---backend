package com.municipality.waste.controller;

import com.municipality.waste.dto.AuthResponse;
import com.municipality.waste.dto.BusinessRegisterRequest;
import com.municipality.waste.dto.LoginRequest;
import com.municipality.waste.dto.RegisterRequest;
import com.municipality.waste.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // Public: sign up a brand-new business (tenant) with its first admin user.
    @PostMapping("/register-business")
    public ResponseEntity<AuthResponse> registerBusiness(@Valid @RequestBody BusinessRegisterRequest request) {
        return ResponseEntity.ok(authService.registerBusiness(request));
    }

    // Admin-only: invite another admin/manager into the caller's own business.
    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.registerStaff(request));
    }
}
