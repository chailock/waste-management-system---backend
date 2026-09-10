package com.municipality.waste.config;

import com.municipality.waste.entity.Business;
import com.municipality.waste.entity.Role;
import com.municipality.waste.entity.User;
import com.municipality.waste.repository.BusinessRepository;
import com.municipality.waste.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeds two accounts on first startup so the system is usable immediately:
 *  - a SUPER_ADMIN (platform operator, oversees every municipality)
 *  - a demo Business with its own ADMIN account
 * Change these credentials after first login in production, or sign up a
 * real municipality via POST /api/auth/register-business.
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedSuperAdmin();
        seedDemoBusiness();
    }

    /** Platform-level account with no business of its own — oversees every municipality. */
    private void seedSuperAdmin() {
        if (userRepository.existsByUsername("superadmin")) {
            return;
        }
        User superAdmin = User.builder()
                .username("superadmin")
                .password(passwordEncoder.encode("SuperAdmin@123"))
                .email("platform@wastems.com")
                .fullName("Platform Administrator")
                .role(Role.SUPER_ADMIN)
                .enabled(true)
                .business(null)
                .build();
        userRepository.save(superAdmin);
    }

    private void seedDemoBusiness() {
        if (userRepository.existsByUsername("admin")) {
            return;
        }

        Business business = Business.builder()
                .name("Demo Municipality")
                .contactEmail("admin@municipality.gov")
                .active(true)
                .build();
        business = businessRepository.save(business);

        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("Admin@123"))
                .email("admin@municipality.gov")
                .fullName("System Administrator")
                .role(Role.ADMIN)
                .enabled(true)
                .business(business)
                .build();
        userRepository.save(admin);
    }
}
