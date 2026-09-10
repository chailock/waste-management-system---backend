package com.municipality.waste.repository;

import com.municipality.waste.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByBusinessIdOrderByCreatedAtDesc(Long businessId);
    Optional<Message> findByIdAndBusinessId(Long id, Long businessId);
    long countByBusinessIdAndReadBySuperAdminFalse(Long businessId);
    long countByBusinessIdAndReadByBusinessFalse(Long businessId);
    List<Message> findByBusinessIdAndReadBySuperAdminFalse(Long businessId);
    List<Message> findByBusinessIdAndReadByBusinessFalse(Long businessId);
}
