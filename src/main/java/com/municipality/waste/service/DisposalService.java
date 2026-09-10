package com.municipality.waste.service;

import com.municipality.waste.entity.Business;
import com.municipality.waste.entity.Disposal;
import com.municipality.waste.exception.ResourceNotFoundException;
import com.municipality.waste.repository.BusinessRepository;
import com.municipality.waste.repository.DisposalRepository;
import com.municipality.waste.repository.WasteCollectionRepository;
import com.municipality.waste.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DisposalService {

    private final DisposalRepository disposalRepository;
    private final WasteCollectionRepository wasteCollectionRepository;
    private final BusinessRepository businessRepository;

    public List<Disposal> findAll() {
        return disposalRepository.findByBusinessId(SecurityUtils.currentBusinessId());
    }

    public List<Disposal> findBetween(LocalDateTime start, LocalDateTime end) {
        return disposalRepository.findByBusinessIdAndDisposalDateBetween(
                SecurityUtils.currentBusinessId(), start, end);
    }

    public Disposal findById(Long id) {
        return disposalRepository.findByIdAndBusinessId(id, SecurityUtils.currentBusinessId())
                .orElseThrow(() -> new ResourceNotFoundException("Disposal record not found with id: " + id));
    }

    public Disposal create(Disposal disposal) {
        resolveCollection(disposal);
        disposal.setBusiness(currentBusiness());
        return disposalRepository.save(disposal);
    }

    public Disposal update(Long id, Disposal updated) {
        Disposal existing = findById(id);
        existing.setQuantity(updated.getQuantity());
        existing.setWasteType(updated.getWasteType());
        existing.setDisposalDate(updated.getDisposalDate());
        existing.setMethod(updated.getMethod());
        existing.setNotes(updated.getNotes());
        resolveCollection(updated);
        existing.setCollection(updated.getCollection());
        return disposalRepository.save(existing);
    }

    public void delete(Long id) {
        disposalRepository.delete(findById(id));
    }

    private void resolveCollection(Disposal disposal) {
        if (disposal.getCollection() != null && disposal.getCollection().getId() != null) {
            disposal.setCollection(wasteCollectionRepository
                    .findByIdAndBusinessId(disposal.getCollection().getId(), SecurityUtils.currentBusinessId())
                    .orElseThrow(() -> new ResourceNotFoundException("Waste collection record not found")));
        } else {
            disposal.setCollection(null);
        }
    }

    private Business currentBusiness() {
        return businessRepository.findById(SecurityUtils.currentBusinessId())
                .orElseThrow(() -> new ResourceNotFoundException("Business not found"));
    }
}
