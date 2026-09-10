package com.municipality.waste.service;

import com.municipality.waste.entity.Business;
import com.municipality.waste.entity.Incident;
import com.municipality.waste.entity.IncidentStatus;
import com.municipality.waste.entity.User;
import com.municipality.waste.exception.ResourceNotFoundException;
import com.municipality.waste.repository.BusinessRepository;
import com.municipality.waste.repository.IncidentRepository;
import com.municipality.waste.repository.UserRepository;
import com.municipality.waste.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;

    public List<Incident> findAll() {
        return incidentRepository.findByBusinessId(SecurityUtils.currentBusinessId());
    }

    public Incident findById(Long id) {
        return incidentRepository.findByIdAndBusinessId(id, SecurityUtils.currentBusinessId())
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found with id: " + id));
    }

    public long countOpenSince(LocalDateTime start, LocalDateTime end) {
        return incidentRepository.countByBusinessIdAndIncidentDateBetween(SecurityUtils.currentBusinessId(), start, end);
    }

    public long countOpen() {
        return incidentRepository.countByBusinessIdAndStatusNot(SecurityUtils.currentBusinessId(), IncidentStatus.RESOLVED);
    }

    public Incident create(Incident incident) {
        // The reporter defaults to whoever is logged in, unless explicitly overridden.
        if (incident.getReportedBy() == null || incident.getReportedBy().getId() == null) {
            incident.setReportedBy(currentUser());
        } else {
            resolveReporter(incident);
        }
        incident.setBusiness(currentBusiness());
        return incidentRepository.save(incident);
    }

    public Incident update(Long id, Incident updated) {
        Incident existing = findById(id);
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setSeverity(updated.getSeverity());
        existing.setIncidentDate(updated.getIncidentDate());
        existing.setLocation(updated.getLocation());
        existing.setStatus(updated.getStatus());
        if (updated.getReportedBy() != null && updated.getReportedBy().getId() != null) {
            resolveReporter(updated);
            existing.setReportedBy(updated.getReportedBy());
        }
        return incidentRepository.save(existing);
    }

    public void delete(Long id) {
        incidentRepository.delete(findById(id));
    }

    private void resolveReporter(Incident incident) {
        incident.setReportedBy(userRepository.findById(incident.getReportedBy().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }

    private User currentUser() {
        return userRepository.findById(SecurityUtils.currentUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Business currentBusiness() {
        return businessRepository.findById(SecurityUtils.currentBusinessId())
                .orElseThrow(() -> new ResourceNotFoundException("Business not found"));
    }
}
