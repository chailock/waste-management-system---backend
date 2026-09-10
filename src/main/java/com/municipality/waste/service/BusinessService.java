package com.municipality.waste.service;

import com.municipality.waste.dto.BusinessSummary;
import com.municipality.waste.entity.Business;
import com.municipality.waste.entity.ContractStatus;
import com.municipality.waste.entity.IncidentStatus;
import com.municipality.waste.entity.RouteStatus;
import com.municipality.waste.entity.WasteCollection;
import com.municipality.waste.exception.BadRequestException;
import com.municipality.waste.exception.ResourceNotFoundException;
import com.municipality.waste.repository.*;
import com.municipality.waste.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;

/**
 * SUPER_ADMIN-only: aggregates every municipality on the platform for the
 * "all municipalities" dashboard. Queries repositories directly (rather
 * than going through the tenant-scoped *Service classes) since it needs to
 * compute figures across many businesses at once, not just the caller's own.
 */
@Service
@RequiredArgsConstructor
public class BusinessService {

    private final BusinessRepository businessRepository;
    private final SiteRepository siteRepository;
    private final VehicleRepository vehicleRepository;
    private final CollectionRouteRepository routeRepository;
    private final IncidentRepository incidentRepository;
    private final ContractRepository contractRepository;
    private final WasteCollectionRepository wasteCollectionRepository;
    private final MessageRepository messageRepository;
    private final AuditService auditService;

    public List<BusinessSummary> findAll() {
        return businessRepository.findAll().stream().map(this::toSummary).toList();
    }

    public BusinessSummary findById(Long id) {
        Business business = businessRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found with id: " + id));
        return toSummary(business);
    }

    /**
     * Suspends or reactivates a municipality. Suspending immediately blocks
     * login for every ADMIN/MANAGER at that business (enforced in
     * UserPrincipal) — it isn't just a cosmetic badge flip.
     */
    @Transactional
    public BusinessSummary setActive(Long id, boolean active) {
        Business business = businessRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found with id: " + id));

        if (business.isActive() == active) {
            throw new BadRequestException(
                    "This municipality is already " + (active ? "active" : "suspended") + ".");
        }

        business.setActive(active);
        business = businessRepository.save(business);

        auditService.record(business, SecurityUtils.currentUser().getUsername(), "SUPER_ADMIN",
                active ? "BUSINESS_REACTIVATED" : "BUSINESS_SUSPENDED",
                (active ? "Reactivated " : "Suspended ") + business.getName()
                        + (active ? " — staff can log in again." : " — staff can no longer log in."));

        return toSummary(business);
    }

    /**
     * Sum of the super admin's own unread messages across every
     * municipality — used for the sidebar's "Municipalities" badge. Cheap
     * on purpose: unlike findAll()/toSummary(), it skips the site/vehicle/
     * route/incident/contract queries entirely, since a polling badge
     * shouldn't pay for a full dashboard computation just for one number.
     */
    public long totalUnreadMessages() {
        return businessRepository.findAll().stream()
                .mapToLong(b -> messageRepository.countByBusinessIdAndReadBySuperAdminFalse(b.getId()))
                .sum();
    }

    private BusinessSummary toSummary(Business business) {
        Long id = business.getId();

        YearMonth currentMonth = YearMonth.now();
        LocalDateTime start = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime end = currentMonth.atEndOfMonth().atTime(23, 59, 59);

        double collected = wasteCollectionRepository.findByBusinessIdAndCollectionDateBetween(id, start, end)
                .stream()
                .map(WasteCollection::getQuantity)
                .filter(Objects::nonNull)
                .mapToDouble(BigDecimal::doubleValue)
                .sum();

        long activeRoutes = routeRepository.findByBusinessId(id).stream()
                .filter(r -> r.getStatus() == RouteStatus.PLANNED || r.getStatus() == RouteStatus.IN_PROGRESS)
                .count();

        long openIncidents = incidentRepository.countByBusinessIdAndStatusNot(id, IncidentStatus.RESOLVED);
        long activeContracts = contractRepository.countByBusinessIdAndStatus(id, ContractStatus.ACTIVE);
        // The super admin's own unread count for this municipality's thread —
        // not whether the municipality itself has read anything.
        long unreadMessages = messageRepository.countByBusinessIdAndReadBySuperAdminFalse(id);

        return BusinessSummary.builder()
                .id(id)
                .name(business.getName())
                .contactEmail(business.getContactEmail())
                .contactPhone(business.getContactPhone())
                .address(business.getAddress())
                .active(business.isActive())
                .createdAt(business.getCreatedAt())
                .siteCount(siteRepository.findByBusinessId(id).size())
                .vehicleCount(vehicleRepository.findByBusinessId(id).size())
                .activeRouteCount(activeRoutes)
                .openIncidentCount(openIncidents)
                .activeContractCount(activeContracts)
                .totalCollectedTonsThisMonth(collected)
                .unreadMessageCount(unreadMessages)
                .build();
    }
}
