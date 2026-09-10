package com.municipality.waste.repository;

import com.municipality.waste.entity.Role;
import com.municipality.waste.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // Recipient resolution for new-message emails: "the other side" of a
    // business<->platform thread. Only enabled users who haven't opted out.
    List<User> findByRoleAndEnabledTrueAndEmailNotificationsEnabledTrue(Role role);
    List<User> findByBusinessIdAndEnabledTrueAndEmailNotificationsEnabledTrue(Long businessId);

    // Full staff roster for a municipality — used to show the super admin
    // who's actually responsible for it (their ADMIN(s)/MANAGER(s)).
    List<User> findByBusinessIdOrderByRoleAscFullNameAsc(Long businessId);
}
