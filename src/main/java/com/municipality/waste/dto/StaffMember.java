package com.municipality.waste.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** One row in the "who's responsible for this municipality" list on the dashboard. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffMember {
    private String fullName;
    private String email;
    private String role;
    private boolean enabled;
    private LocalDateTime lastLoginAt; // null = never logged in
}
