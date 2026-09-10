package com.municipality.waste.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * documentType is a free-form label so operators can add their own
 * categories. Suggested values (shown as a dropdown on the frontend):
 * Waste Management Licence, Insurance Certificate, Vehicle Licence,
 * Roadworthy Certificate, Training Certificate, Environmental Permit,
 * Contract, Other.
 */
@Entity
@Table(name = "compliance_documents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplianceDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "document_name", nullable = false)
    private String documentName;

    @Column(name = "document_type")
    private String documentType;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Builder.Default
    private String status = "VALID"; // VALID, EXPIRING_SOON, EXPIRED

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
