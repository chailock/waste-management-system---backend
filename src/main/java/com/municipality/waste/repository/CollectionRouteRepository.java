package com.municipality.waste.repository;

import com.municipality.waste.entity.CollectionRoute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CollectionRouteRepository extends JpaRepository<CollectionRoute, Long> {
    List<CollectionRoute> findByBusinessId(Long businessId);
    Optional<CollectionRoute> findByIdAndBusinessId(Long id, Long businessId);
}
