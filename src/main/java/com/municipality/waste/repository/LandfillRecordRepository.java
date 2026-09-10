package com.municipality.waste.repository;

import com.municipality.waste.entity.LandfillRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LandfillRecordRepository extends JpaRepository<LandfillRecord, Long> {
    List<LandfillRecord> findByBusinessId(Long businessId);
    Optional<LandfillRecord> findByIdAndBusinessId(Long id, Long businessId);
    boolean existsByTicketNumber(String ticketNumber);
}
