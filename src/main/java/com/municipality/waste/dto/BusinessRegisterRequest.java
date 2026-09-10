package com.municipality.waste.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Public signup: creates a brand-new Business (tenant) along with its
 * first user, who is automatically made ADMIN of that business.
 */
@Data
public class BusinessRegisterRequest {

    @NotBlank
    private String businessName;

    private String registrationNumber;
    private String contactPhone;
    private String address;

    @NotBlank
    private String username;

    @NotBlank
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String fullName;
}
