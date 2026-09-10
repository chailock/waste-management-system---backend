package com.municipality.waste.repository;

import com.municipality.waste.entity.ComplianceDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ComplianceDocumentRepository extends JpaRepository<ComplianceDocument, Long> {
    List<ComplianceDocument> findByBusinessId(Long businessId);
    Optional<ComplianceDocument> findByIdAndBusinessId(Long id, Long businessId);
}
