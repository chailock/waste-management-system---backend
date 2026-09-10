package com.municipality.waste.repository;

import com.municipality.waste.entity.Disposal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DisposalRepository extends JpaRepository<Disposal, Long> {
    List<Disposal> findByBusinessId(Long businessId);
    Optional<Disposal> findByIdAndBusinessId(Long id, Long businessId);
    List<Disposal> findByBusinessIdAndDisposalDateBetween(Long businessId, LocalDateTime start, LocalDateTime end);
}
