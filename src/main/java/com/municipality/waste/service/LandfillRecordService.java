package com.municipality.waste.service;

import com.municipality.waste.entity.Business;
import com.municipality.waste.entity.LandfillRecord;
import com.municipality.waste.exception.BadRequestException;
import com.municipality.waste.exception.ResourceNotFoundException;
import com.municipality.waste.repository.BusinessRepository;
import com.municipality.waste.repository.DisposalRepository;
import com.municipality.waste.repository.LandfillRecordRepository;
import com.municipality.waste.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LandfillRecordService {

    private final LandfillRecordRepository landfillRecordRepository;
    private final DisposalRepository disposalRepository;
    private final BusinessRepository businessRepository;

    public List<LandfillRecord> findAll() {
        return landfillRecordRepository.findByBusinessId(SecurityUtils.currentBusinessId());
    }

    public LandfillRecord findById(Long id) {
        return landfillRecordRepository.findByIdAndBusinessId(id, SecurityUtils.currentBusinessId())
                .orElseThrow(() -> new ResourceNotFoundException("Landfill record not found with id: " + id));
    }

    public LandfillRecord create(LandfillRecord record) {
        validateTicketNumber(record, null);
        resolveDisposal(record);
        record.setBusiness(currentBusiness());
        return landfillRecordRepository.save(record);
    }

    public LandfillRecord update(Long id, LandfillRecord updated) {
        LandfillRecord existing = findById(id);
        validateTicketNumber(updated, id);
        existing.setLandfillName(updated.getLandfillName());
        existing.setLandfillLocation(updated.getLandfillLocation());
        existing.setWeighbridgeNumber(updated.getWeighbridgeNumber());
        existing.setQuantity(updated.getQuantity());
        existing.setWasteType(updated.getWasteType());
        existing.setDisposalDate(updated.getDisposalDate());
        existing.setTicketNumber(updated.getTicketNumber());
        existing.setNotes(updated.getNotes());
        resolveDisposal(updated);
        existing.setDisposal(updated.getDisposal());
        return landfillRecordRepository.save(existing);
    }

    public void delete(Long id) {
        landfillRecordRepository.delete(findById(id));
    }

    private void resolveDisposal(LandfillRecord record) {
        if (record.getDisposal() != null && record.getDisposal().getId() != null) {
            record.setDisposal(disposalRepository
                    .findByIdAndBusinessId(record.getDisposal().getId(), SecurityUtils.currentBusinessId())
                    .orElseThrow(() -> new ResourceNotFoundException("Disposal record not found")));
        } else {
            record.setDisposal(null);
        }
    }

    private void validateTicketNumber(LandfillRecord record, Long excludingId) {
        if (record.getTicketNumber() == null || record.getTicketNumber().isBlank()) {
            return;
        }
        boolean exists = landfillRecordRepository.existsByTicketNumber(record.getTicketNumber());
        if (exists) {
            // Re-check it isn't just the same record being re-saved unchanged.
            boolean isSameRecord = excludingId != null
                    && landfillRecordRepository.findById(excludingId)
                        .map(r -> record.getTicketNumber().equalsIgnoreCase(r.getTicketNumber()))
                        .orElse(false);
            if (!isSameRecord) {
                throw new BadRequestException("Ticket number " + record.getTicketNumber() + " is already recorded");
            }
        }
    }

    private Business currentBusiness() {
        return businessRepository.findById(SecurityUtils.currentBusinessId())
                .orElseThrow(() -> new ResourceNotFoundException("Business not found"));
    }
}
