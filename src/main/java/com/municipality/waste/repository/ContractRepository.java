package com.municipality.waste.repository;

import com.municipality.waste.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    List<Contract> findByBusinessId(Long businessId);
    Optional<Contract> findByIdAndBusinessId(Long id, Long businessId);
    long countByBusinessIdAndStatus(Long businessId, com.municipality.waste.entity.ContractStatus status);
}
