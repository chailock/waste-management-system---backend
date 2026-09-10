package com.municipality.waste.repository;

import com.municipality.waste.entity.MonthlyReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MonthlyReportRepository extends JpaRepository<MonthlyReport, Long> {
    List<MonthlyReport> findByBusinessId(Long businessId);
    Optional<MonthlyReport> findByIdAndBusinessId(Long id, Long businessId);
    Optional<MonthlyReport> findByBusinessIdAndMonthAndYear(Long businessId, Integer month, Integer year);
}
