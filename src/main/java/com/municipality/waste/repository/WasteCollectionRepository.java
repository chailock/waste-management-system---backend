package com.municipality.waste.repository;

import com.municipality.waste.entity.WasteCollection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WasteCollectionRepository extends JpaRepository<WasteCollection, Long> {
    List<WasteCollection> findByBusinessId(Long businessId);
    Optional<WasteCollection> findByIdAndBusinessId(Long id, Long businessId);
    List<WasteCollection> findByBusinessIdAndCollectionDateBetween(Long businessId, LocalDateTime start, LocalDateTime end);
}
