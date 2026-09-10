package com.municipality.waste.repository;

import com.municipality.waste.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByBusinessId(Long businessId);
    Optional<Vehicle> findByIdAndBusinessId(Long id, Long businessId);
}
