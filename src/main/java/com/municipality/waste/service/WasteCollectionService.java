package com.municipality.waste.service;

import com.municipality.waste.entity.Business;
import com.municipality.waste.entity.WasteCollection;
import com.municipality.waste.exception.ResourceNotFoundException;
import com.municipality.waste.repository.BusinessRepository;
import com.municipality.waste.repository.CollectionRouteRepository;
import com.municipality.waste.repository.SiteRepository;
import com.municipality.waste.repository.WasteCollectionRepository;
import com.municipality.waste.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WasteCollectionService {

    private final WasteCollectionRepository wasteCollectionRepository;
    private final SiteRepository siteRepository;
    private final CollectionRouteRepository routeRepository;
    private final BusinessRepository businessRepository;

    public List<WasteCollection> findAll() {
        return wasteCollectionRepository.findByBusinessId(SecurityUtils.currentBusinessId());
    }

    public List<WasteCollection> findBetween(LocalDateTime start, LocalDateTime end) {
        return wasteCollectionRepository.findByBusinessIdAndCollectionDateBetween(
                SecurityUtils.currentBusinessId(), start, end);
    }

    public WasteCollection findById(Long id) {
        return wasteCollectionRepository.findByIdAndBusinessId(id, SecurityUtils.currentBusinessId())
                .orElseThrow(() -> new ResourceNotFoundException("Waste collection record not found with id: " + id));
    }

    public WasteCollection create(WasteCollection entry) {
        resolveRelations(entry);
        entry.setBusiness(currentBusiness());
        return wasteCollectionRepository.save(entry);
    }

    public WasteCollection update(Long id, WasteCollection updated) {
        WasteCollection existing = findById(id);
        existing.setQuantity(updated.getQuantity());
        existing.setUnit(updated.getUnit());
        existing.setWasteType(updated.getWasteType());
        existing.setCollectionDate(updated.getCollectionDate());
        existing.setNotes(updated.getNotes());
        resolveRelations(updated);
        existing.setSite(updated.getSite());
        existing.setRoute(updated.getRoute());
        return wasteCollectionRepository.save(existing);
    }

    public void delete(Long id) {
        wasteCollectionRepository.delete(findById(id));
    }

    private void resolveRelations(WasteCollection entry) {
        Long businessId = SecurityUtils.currentBusinessId();
        if (entry.getSite() != null && entry.getSite().getId() != null) {
            entry.setSite(siteRepository.findByIdAndBusinessId(entry.getSite().getId(), businessId)
                    .orElseThrow(() -> new ResourceNotFoundException("Site not found")));
        } else {
            entry.setSite(null);
        }
        if (entry.getRoute() != null && entry.getRoute().getId() != null) {
            entry.setRoute(routeRepository.findByIdAndBusinessId(entry.getRoute().getId(), businessId)
                    .orElseThrow(() -> new ResourceNotFoundException("Collection route not found")));
        } else {
            entry.setRoute(null);
        }
    }

    private Business currentBusiness() {
        return businessRepository.findById(SecurityUtils.currentBusinessId())
                .orElseThrow(() -> new ResourceNotFoundException("Business not found"));
    }
}
