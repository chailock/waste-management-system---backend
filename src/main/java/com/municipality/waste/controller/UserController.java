package com.municipality.waste.controller;

import com.municipality.waste.dto.MeResponse;
import com.municipality.waste.dto.NotificationPreferenceRequest;
import com.municipality.waste.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<MeResponse> me() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @PatchMapping("/me/notifications")
    public ResponseEntity<MeResponse> updateNotificationPreference(@RequestBody NotificationPreferenceRequest request) {
        return ResponseEntity.ok(userService.setEmailNotificationsEnabled(request.isEmailNotificationsEnabled()));
    }
}
