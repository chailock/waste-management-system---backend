package com.municipality.waste.repository;

import com.municipality.waste.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findByRelatedEntityTypeAndRelatedEntityIdAndBusinessId(String relatedEntityType, Long relatedEntityId, Long businessId);
    List<Photo> findByBusinessId(Long businessId);
    Optional<Photo> findByIdAndBusinessId(Long id, Long businessId);
}
