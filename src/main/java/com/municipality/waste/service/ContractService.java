package com.municipality.waste.service;

import com.municipality.waste.entity.Business;
import com.municipality.waste.entity.Contract;
import com.municipality.waste.entity.ContractStatus;
import com.municipality.waste.exception.ResourceNotFoundException;
import com.municipality.waste.repository.BusinessRepository;
import com.municipality.waste.repository.ContractRepository;
import com.municipality.waste.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository contractRepository;
    private final BusinessRepository businessRepository;

    public List<Contract> findAll() {
        return contractRepository.findByBusinessId(SecurityUtils.currentBusinessId());
    }

    public Contract findById(Long id) {
        return contractRepository.findByIdAndBusinessId(id, SecurityUtils.currentBusinessId())
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found with id: " + id));
    }

    public long countByStatus(ContractStatus status) {
        return contractRepository.countByBusinessIdAndStatus(SecurityUtils.currentBusinessId(), status);
    }

    public Contract create(Contract contract) {
        contract.setBusiness(currentBusiness());
        return contractRepository.save(contract);
    }

    public Contract update(Long id, Contract updated) {
        Contract existing = findById(id);
        existing.setContractNumber(updated.getContractNumber());
        existing.setClientName(updated.getClientName());
        existing.setDescription(updated.getDescription());
        existing.setStartDate(updated.getStartDate());
        existing.setEndDate(updated.getEndDate());
        existing.setMonthlyValue(updated.getMonthlyValue());
        existing.setStatus(updated.getStatus());
        return contractRepository.save(existing);
    }

    public void delete(Long id) {
        contractRepository.delete(findById(id));
    }

    private Business currentBusiness() {
        return businessRepository.findById(SecurityUtils.currentBusinessId())
                .orElseThrow(() -> new ResourceNotFoundException("Business not found"));
    }
}
