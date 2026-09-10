package com.municipality.waste.service;

import com.municipality.waste.dto.DashboardStats;
import com.municipality.waste.dto.StaffMember;
import com.municipality.waste.entity.ContractStatus;
import com.municipality.waste.entity.Disposal;
import com.municipality.waste.entity.RouteStatus;
import com.municipality.waste.entity.User;
import com.municipality.waste.entity.WasteCollection;
import com.municipality.waste.repository.CollectionRouteRepository;
import com.municipality.waste.repository.SiteRepository;
import com.municipality.waste.repository.UserRepository;
import com.municipality.waste.repository.VehicleRepository;
import com.municipality.waste.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SiteRepository siteRepository;
    private final VehicleRepository vehicleRepository;
    private final CollectionRouteRepository routeRepository;
    private final UserRepository userRepository;
    private final IncidentService incidentService;
    private final ContractService contractService;
    private final WasteCollectionService wasteCollectionService;
    private final DisposalService disposalService;
    private final ComplianceDocumentService complianceDocumentService;

    public DashboardStats getStats() {
        Long businessId = SecurityUtils.currentBusinessId();
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime start = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime end = currentMonth.atEndOfMonth().atTime(23, 59, 59);

        List<WasteCollection> collections = wasteCollectionService.findBetween(start, end);
        double collected = collections.stream()
                .map(WasteCollection::getQuantity)
                .filter(Objects::nonNull)
                .mapToDouble(BigDecimal::doubleValue)
                .sum();

        List<Disposal> disposals = disposalService.findBetween(start, end);
        double disposed = disposals.stream()
                .map(Disposal::getQuantity)
                .filter(Objects::nonNull)
                .mapToDouble(BigDecimal::doubleValue)
                .sum();

        long activeRoutes = routeRepository.findByBusinessId(businessId).stream()
                .filter(r -> r.getStatus() == RouteStatus.PLANNED || r.getStatus() == RouteStatus.IN_PROGRESS)
                .count();

        long activeContracts = contractService.countByStatus(ContractStatus.ACTIVE);
        long expiringDocs = complianceDocumentService.countExpiringWithin(30);

        List<StaffMember> staff = userRepository.findByBusinessIdOrderByRoleAscFullNameAsc(businessId).stream()
                .map(this::toStaffMember)
                .toList();

        return DashboardStats.builder()
                .totalSites(siteRepository.findByBusinessId(businessId).size())
                .totalVehicles(vehicleRepository.findByBusinessId(businessId).size())
                .activeRoutes(activeRoutes)
                .openIncidents(incidentService.countOpen())
                .activeContracts(activeContracts)
                .totalCollectedTonsThisMonth(collected)
                .totalDisposedTonsThisMonth(disposed)
                .expiringComplianceDocuments(expiringDocs)
                .staff(staff)
                .build();
    }

    private StaffMember toStaffMember(User user) {
        return StaffMember.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .enabled(user.isEnabled())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }
}
