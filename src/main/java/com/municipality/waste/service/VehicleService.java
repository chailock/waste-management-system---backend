package com.municipality.waste.service;

import com.municipality.waste.entity.Business;
import com.municipality.waste.entity.Vehicle;
import com.municipality.waste.exception.ResourceNotFoundException;
import com.municipality.waste.repository.BusinessRepository;
import com.municipality.waste.repository.VehicleRepository;
import com.municipality.waste.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final BusinessRepository businessRepository;

    public List<Vehicle> findAll() {
        return vehicleRepository.findByBusinessId(SecurityUtils.currentBusinessId());
    }

    public Vehicle findById(Long id) {
        return vehicleRepository.findByIdAndBusinessId(id, SecurityUtils.currentBusinessId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));
    }

    public Vehicle create(Vehicle vehicle) {
        vehicle.setBusiness(currentBusiness());
        return vehicleRepository.save(vehicle);
    }

    public Vehicle update(Long id, Vehicle updated) {
        Vehicle existing = findById(id);
        existing.setRegistrationNumber(updated.getRegistrationNumber());
        existing.setMake(updated.getMake());
        existing.setModel(updated.getModel());
        existing.setCapacity(updated.getCapacity());
        existing.setStatus(updated.getStatus());
        existing.setLicenseExpiry(updated.getLicenseExpiry());
        existing.setRoadworthyExpiry(updated.getRoadworthyExpiry());
        return vehicleRepository.save(existing);
    }

    public void delete(Long id) {
        vehicleRepository.delete(findById(id));
    }

    private Business currentBusiness() {
        return businessRepository.findById(SecurityUtils.currentBusinessId())
                .orElseThrow(() -> new ResourceNotFoundException("Business not found"));
    }
}
