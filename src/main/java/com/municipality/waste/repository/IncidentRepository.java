package com.municipality.waste.repository;

import com.municipality.waste.entity.Incident;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IncidentRepository extends JpaRepository<Incident, Long> {
    List<Incident> findByBusinessId(Long businessId);
    Optional<Incident> findByIdAndBusinessId(Long id, Long businessId);
    long countByBusinessIdAndIncidentDateBetween(Long businessId, LocalDateTime start, LocalDateTime end);
    long countByBusinessIdAndStatusNot(Long businessId, com.municipality.waste.entity.IncidentStatus status);
}
