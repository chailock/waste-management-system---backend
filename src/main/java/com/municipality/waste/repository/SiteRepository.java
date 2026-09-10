package com.municipality.waste.repository;

import com.municipality.waste.entity.Site;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SiteRepository extends JpaRepository<Site, Long> {
    List<Site> findByBusinessId(Long businessId);
    Optional<Site> findByIdAndBusinessId(Long id, Long businessId);
}
