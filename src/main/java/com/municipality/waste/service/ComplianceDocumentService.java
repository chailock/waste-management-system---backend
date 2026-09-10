package com.municipality.waste.service;

import com.municipality.waste.entity.Business;
import com.municipality.waste.entity.ComplianceDocument;
import com.municipality.waste.exception.ResourceNotFoundException;
import com.municipality.waste.repository.BusinessRepository;
import com.municipality.waste.repository.ComplianceDocumentRepository;
import com.municipality.waste.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplianceDocumentService {

    private final ComplianceDocumentRepository complianceDocumentRepository;
    private final BusinessRepository businessRepository;

    public List<ComplianceDocument> findAll() {
        return complianceDocumentRepository.findByBusinessId(SecurityUtils.currentBusinessId());
    }

    public ComplianceDocument findById(Long id) {
        return complianceDocumentRepository.findByIdAndBusinessId(id, SecurityUtils.currentBusinessId())
                .orElseThrow(() -> new ResourceNotFoundException("Compliance document not found with id: " + id));
    }

    public long countExpiringWithin(int days) {
        LocalDate soon = LocalDate.now().plusDays(days);
        return findAll().stream()
                .filter(d -> d.getExpiryDate() != null
                        && !d.getExpiryDate().isBefore(LocalDate.now())
                        && !d.getExpiryDate().isAfter(soon))
                .count();
    }

    public ComplianceDocument create(ComplianceDocument doc) {
        applyDerivedStatus(doc);
        doc.setBusiness(currentBusiness());
        return complianceDocumentRepository.save(doc);
    }

    public ComplianceDocument update(Long id, ComplianceDocument updated) {
        ComplianceDocument existing = findById(id);
        existing.setDocumentName(updated.getDocumentName());
        existing.setDocumentType(updated.getDocumentType());
        existing.setFileName(updated.getFileName());
        existing.setFileUrl(updated.getFileUrl());
        existing.setIssueDate(updated.getIssueDate());
        existing.setExpiryDate(updated.getExpiryDate());
        applyDerivedStatus(existing);
        return complianceDocumentRepository.save(existing);
    }

    public void delete(Long id) {
        complianceDocumentRepository.delete(findById(id));
    }

    /** VALID / EXPIRING_SOON (30 days) / EXPIRED, computed from the expiry date. */
    private void applyDerivedStatus(ComplianceDocument doc) {
        if (doc.getExpiryDate() == null) {
            doc.setStatus("VALID");
            return;
        }
        LocalDate today = LocalDate.now();
        if (doc.getExpiryDate().isBefore(today)) {
            doc.setStatus("EXPIRED");
        } else if (!doc.getExpiryDate().isAfter(today.plusDays(30))) {
            doc.setStatus("EXPIRING_SOON");
        } else {
            doc.setStatus("VALID");
        }
    }

    private Business currentBusiness() {
        return businessRepository.findById(SecurityUtils.currentBusinessId())
                .orElseThrow(() -> new ResourceNotFoundException("Business not found"));
    }
}
