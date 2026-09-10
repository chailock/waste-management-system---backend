package com.municipality.waste.service;

import com.municipality.waste.entity.Business;
import com.municipality.waste.entity.Disposal;
import com.municipality.waste.entity.MonthlyReport;
import com.municipality.waste.entity.WasteCollection;
import com.municipality.waste.exception.ResourceNotFoundException;
import com.municipality.waste.repository.BusinessRepository;
import com.municipality.waste.repository.DisposalRepository;
import com.municipality.waste.repository.MonthlyReportRepository;
import com.municipality.waste.repository.WasteCollectionRepository;
import com.municipality.waste.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

/**
 * Figures are aggregated as raw quantity values regardless of each record's
 * unit (TON/KG). In practice all collections in a given deployment use the
 * same unit (TON by default), so a straight sum is accurate; if a business
 * ever mixes units this would need a proper conversion step.
 */
@Service
@RequiredArgsConstructor
public class MonthlyReportService {

    private final MonthlyReportRepository monthlyReportRepository;
    private final WasteCollectionRepository wasteCollectionRepository;
    private final DisposalRepository disposalRepository;
    private final IncidentService incidentService;
    private final BusinessRepository businessRepository;

    public List<MonthlyReport> findAll() {
        return monthlyReportRepository.findByBusinessId(SecurityUtils.currentBusinessId());
    }

    public MonthlyReport findById(Long id) {
        return monthlyReportRepository.findByIdAndBusinessId(id, SecurityUtils.currentBusinessId())
                .orElseThrow(() -> new ResourceNotFoundException("Monthly report not found with id: " + id));
    }

    public MonthlyReport generate(Integer month, Integer year, String generatedBy, String notes) {
        Long businessId = SecurityUtils.currentBusinessId();
        YearMonth ym = YearMonth.of(year, month);
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.atEndOfMonth().atTime(23, 59, 59);

        double totalCollected = wasteCollectionRepository
                .findByBusinessIdAndCollectionDateBetween(businessId, start, end)
                .stream()
                .map(WasteCollection::getQuantity)
                .filter(java.util.Objects::nonNull)
                .mapToDouble(BigDecimal::doubleValue)
                .sum();

        double totalDisposed = disposalRepository
                .findByBusinessIdAndDisposalDateBetween(businessId, start, end)
                .stream()
                .map(Disposal::getQuantity)
                .filter(java.util.Objects::nonNull)
                .mapToDouble(BigDecimal::doubleValue)
                .sum();

        long incidentsCount = incidentService.countOpenSince(start, end);

        MonthlyReport report = monthlyReportRepository.findByBusinessIdAndMonthAndYear(businessId, month, year)
                .orElse(MonthlyReport.builder().month(month).year(year).business(currentBusiness()).build());

        report.setTotalCollectedQty(totalCollected);
        report.setTotalDisposedQty(totalDisposed);
        report.setIncidentsCount((int) incidentsCount);
        report.setGeneratedBy(generatedBy);
        report.setNotes(notes);

        return monthlyReportRepository.save(report);
    }

    public void delete(Long id) {
        monthlyReportRepository.delete(findById(id));
    }

    private Business currentBusiness() {
        return businessRepository.findById(SecurityUtils.currentBusinessId())
                .orElseThrow(() -> new ResourceNotFoundException("Business not found"));
    }
}
