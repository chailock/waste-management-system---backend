package com.municipality.waste.service;

import com.municipality.waste.entity.Business;
import com.municipality.waste.entity.Site;
import com.municipality.waste.exception.ResourceNotFoundException;
import com.municipality.waste.repository.BusinessRepository;
import com.municipality.waste.repository.SiteRepository;
import com.municipality.waste.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SiteService {

    private final SiteRepository siteRepository;
    private final BusinessRepository businessRepository;

    public List<Site> findAll() {
        return siteRepository.findByBusinessId(SecurityUtils.currentBusinessId());
    }

    public Site findById(Long id) {
        return siteRepository.findByIdAndBusinessId(id, SecurityUtils.currentBusinessId())
                .orElseThrow(() -> new ResourceNotFoundException("Site not found with id: " + id));
    }

    public Site create(Site site) {
        site.setBusiness(currentBusiness());
        return siteRepository.save(site);
    }

    public Site update(Long id, Site updated) {
        Site existing = findById(id);
        existing.setName(updated.getName());
        existing.setAddress(updated.getAddress());
        existing.setContactPerson(updated.getContactPerson());
        existing.setContactPhone(updated.getContactPhone());
        existing.setWasteType(updated.getWasteType());
        existing.setActive(updated.isActive());
        return siteRepository.save(existing);
    }

    public void delete(Long id) {
        siteRepository.delete(findById(id));
    }

    private Business currentBusiness() {
        return businessRepository.findById(SecurityUtils.currentBusinessId())
                .orElseThrow(() -> new ResourceNotFoundException("Business not found"));
    }
}
