package com.municipality.waste.service;

import com.municipality.waste.dto.AuthResponse;
import com.municipality.waste.dto.BusinessRegisterRequest;
import com.municipality.waste.dto.LoginRequest;
import com.municipality.waste.dto.RegisterRequest;
import com.municipality.waste.entity.Business;
import com.municipality.waste.entity.Role;
import com.municipality.waste.entity.User;
import com.municipality.waste.exception.BadRequestException;
import com.municipality.waste.repository.BusinessRepository;
import com.municipality.waste.repository.UserRepository;
import com.municipality.waste.security.JwtUtil;
import com.municipality.waste.security.SecurityUtils;
import com.municipality.waste.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuditService auditService;

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String token = jwtUtil.generateToken(authentication);

        User user = userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new BadRequestException("User not found"));

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
        auditService.record(user.getBusiness(), user.getUsername(), user.getRole().name(),
                "LOGIN", user.getFullName() + " logged in");

        return AuthResponse.builder()
                .token(token)
                .id(principal.getId())
                .username(principal.getUsername())
                .fullName(principal.getFullName())
                .email(user.getEmail())
                .role(principal.getRole())
                .businessId(principal.getBusinessId())
                .businessName(user.getBusiness() != null ? user.getBusiness().getName() : null)
                .build();
    }

    /**
     * Public signup — creates a brand-new tenant (Business) plus its first
     * user, who becomes that business's ADMIN. This is the only way a new
     * business gets onto the platform; there is no "join an existing
     * business" self-service flow — an existing ADMIN must invite staff
     * via {@link #registerStaff}.
     */
    @Transactional
    public AuthResponse registerBusiness(BusinessRegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }

        Business business = Business.builder()
                .name(request.getBusinessName())
                .registrationNumber(request.getRegistrationNumber())
                .contactEmail(request.getEmail())
                .contactPhone(request.getContactPhone())
                .address(request.getAddress())
                .active(true)
                .build();
        business = businessRepository.save(business);

        User admin = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .fullName(request.getFullName())
                .role(Role.ADMIN)
                .enabled(true)
                .business(business)
                .build();
        userRepository.save(admin);
        auditService.record(business, admin.getUsername(), admin.getRole().name(),
                "BUSINESS_CREATED", business.getName() + " signed up on the platform");

        return AuthResponse.builder()
                .username(admin.getUsername())
                .fullName(admin.getFullName())
                .email(admin.getEmail())
                .role(admin.getRole().name())
                .businessId(business.getId())
                .businessName(business.getName())
                .build();
    }

    /**
     * Admin-only — adds another Admin or Manager to the caller's own
     * business. Enforced at the controller with @PreAuthorize; the
     * business is always taken from the caller's own token, never from
     * client input, so an admin can never create a user in someone else's
     * business.
     */
    public AuthResponse registerStaff(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }

        Role role;
        try {
            role = Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Role must be either ADMIN or MANAGER");
        }
        // SUPER_ADMIN is a platform-level role, never tied to a business — it
        // must never be grantable through a business's own staff-invite flow.
        if (role == Role.SUPER_ADMIN) {
            throw new BadRequestException("Role must be either ADMIN or MANAGER");
        }

        Business business = businessRepository.findById(SecurityUtils.currentBusinessId())
                .orElseThrow(() -> new BadRequestException("Business not found"));

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .fullName(request.getFullName())
                .role(role)
                .enabled(true)
                .business(business)
                .build();

        User saved = userRepository.save(user);
        auditService.record(business, saved.getUsername(), saved.getRole().name(),
                "STAFF_CREATED", saved.getFullName() + " (" + saved.getRole().name() + ") was added to " + business.getName());

        return AuthResponse.builder()
                .id(saved.getId())
                .username(saved.getUsername())
                .fullName(saved.getFullName())
                .email(saved.getEmail())
                .role(saved.getRole().name())
                .businessId(business.getId())
                .businessName(business.getName())
                .build();
    }

    /**
     * SUPER_ADMIN-only recovery path: creates the first staff account for a
     * municipality that currently has none. Deliberately narrow — this is
     * NOT a general "super admin can add users anywhere" backdoor. It only
     * works when the target business has zero existing staff, so it can
     * only ever unblock a municipality that's otherwise inaccessible to
     * itself, never bypass an existing admin's authority to manage their
     * own team.
     */
    @Transactional
    public AuthResponse registerStaffForBusiness(Long businessId, RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new BadRequestException("Business not found"));

        if (!userRepository.findByBusinessIdOrderByRoleAscFullNameAsc(businessId).isEmpty()) {
            throw new BadRequestException(
                    "This municipality already has staff. Ask one of their existing admins to add more, "
                            + "or disable/remove their account first if it's truly locked out.");
        }

        Role role;
        try {
            role = Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Role must be either ADMIN or MANAGER");
        }
        if (role == Role.SUPER_ADMIN) {
            throw new BadRequestException("Role must be either ADMIN or MANAGER");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .fullName(request.getFullName())
                .role(role)
                .enabled(true)
                .business(business)
                .build();

        User saved = userRepository.save(user);
        auditService.record(business, SecurityUtils.currentUser().getUsername(), "SUPER_ADMIN",
                "STAFF_CREATED",
                "Platform administrator created the first staff account (" + saved.getFullName()
                        + ", " + saved.getRole().name() + ") for " + business.getName());

        return AuthResponse.builder()
                .id(saved.getId())
                .username(saved.getUsername())
                .fullName(saved.getFullName())
                .email(saved.getEmail())
                .role(saved.getRole().name())
                .businessId(business.getId())
                .businessName(business.getName())
                .build();
    }
}
