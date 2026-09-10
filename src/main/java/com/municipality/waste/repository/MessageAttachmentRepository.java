package com.municipality.waste.repository;

import com.municipality.waste.entity.MessageAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MessageAttachmentRepository extends JpaRepository<MessageAttachment, Long> {
    List<MessageAttachment> findByMessageId(Long messageId);
    Optional<MessageAttachment> findByIdAndMessageId(Long id, Long messageId);
}
