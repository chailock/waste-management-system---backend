package com.municipality.waste.service;

import com.municipality.waste.entity.Business;
import com.municipality.waste.entity.CollectionRoute;
import com.municipality.waste.exception.ResourceNotFoundException;
import com.municipality.waste.repository.BusinessRepository;
import com.municipality.waste.repository.CollectionRouteRepository;
import com.municipality.waste.repository.VehicleRepository;
import com.municipality.waste.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CollectionRouteService {

    private final CollectionRouteRepository routeRepository;
    private final VehicleRepository vehicleRepository;
    private final BusinessRepository businessRepository;

    public List<CollectionRoute> findAll() {
        return routeRepository.findByBusinessId(SecurityUtils.currentBusinessId());
    }

    public CollectionRoute findById(Long id) {
        return routeRepository.findByIdAndBusinessId(id, SecurityUtils.currentBusinessId())
                .orElseThrow(() -> new ResourceNotFoundException("Collection route not found with id: " + id));
    }

    public CollectionRoute create(CollectionRoute route) {
        resolveVehicle(route);
        route.setBusiness(currentBusiness());
        return routeRepository.save(route);
    }

    public CollectionRoute update(Long id, CollectionRoute updated) {
        CollectionRoute existing = findById(id);
        existing.setName(updated.getName());
        existing.setRouteDate(updated.getRouteDate());
        existing.setStatus(updated.getStatus());
        existing.setDriverName(updated.getDriverName());
        existing.setDriverPhone(updated.getDriverPhone());
        resolveVehicle(updated);
        existing.setVehicle(updated.getVehicle());
        return routeRepository.save(existing);
    }

    public void delete(Long id) {
        routeRepository.delete(findById(id));
    }

    private void resolveVehicle(CollectionRoute route) {
        if (route.getVehicle() != null && route.getVehicle().getId() != null) {
            Long businessId = SecurityUtils.currentBusinessId();
            route.setVehicle(vehicleRepository.findByIdAndBusinessId(route.getVehicle().getId(), businessId)
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found")));
        } else {
            route.setVehicle(null);
        }
    }

    private Business currentBusiness() {
        return businessRepository.findById(SecurityUtils.currentBusinessId())
                .orElseThrow(() -> new ResourceNotFoundException("Business not found"));
    }
}
