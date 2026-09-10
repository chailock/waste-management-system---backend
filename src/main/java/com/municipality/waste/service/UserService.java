package com.municipality.waste.service;

import com.municipality.waste.dto.MeResponse;
import com.municipality.waste.entity.User;
import com.municipality.waste.exception.ResourceNotFoundException;
import com.municipality.waste.repository.UserRepository;
import com.municipality.waste.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public MeResponse getCurrentUser() {
        User user = loadCurrentUser();
        return toResponse(user);
    }

    public MeResponse setEmailNotificationsEnabled(boolean enabled) {
        User user = loadCurrentUser();
        user.setEmailNotificationsEnabled(enabled);
        user = userRepository.save(user);
        return toResponse(user);
    }

    private User loadCurrentUser() {
        Long id = SecurityUtils.currentUser().getId();
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private MeResponse toResponse(User user) {
        return MeResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .emailNotificationsEnabled(user.isEmailNotificationsEnabled())
                .build();
    }
}
