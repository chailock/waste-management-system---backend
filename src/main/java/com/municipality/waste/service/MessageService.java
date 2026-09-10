package com.municipality.waste.service;

import com.municipality.waste.entity.*;
import com.municipality.waste.exception.ResourceNotFoundException;
import com.municipality.waste.repository.BusinessRepository;
import com.municipality.waste.repository.MessageAttachmentRepository;
import com.municipality.waste.repository.MessageRepository;
import com.municipality.waste.repository.UserRepository;
import com.municipality.waste.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageAttachmentRepository attachmentRepository;
    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final FileStorageService fileStorageService;
    private final EmailService emailService;

    /**
     * Loads the whole thread and, in the same call, marks everything the
     * *other* side sent as read on the caller's side — this is the "opening
     * your inbox clears the badge" behavior. A dedicated, side-effect-free
     * count is available via unreadCount() for polling badges without
     * accidentally clearing them just by checking.
     */
    public List<Message> findAll() {
        Long businessId = SecurityUtils.currentBusinessId();
        markThreadReadForCaller(businessId);
        return messageRepository.findByBusinessIdOrderByCreatedAtDesc(businessId);
    }

    public Message findById(Long id) {
        Message message = messageRepository.findByIdAndBusinessId(id, SecurityUtils.currentBusinessId())
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));

        if (SecurityUtils.isSuperAdmin() && !message.isReadBySuperAdmin()) {
            message.setReadBySuperAdmin(true);
            message = messageRepository.save(message);
        } else if (!SecurityUtils.isSuperAdmin() && !message.isReadByBusiness()) {
            message.setReadByBusiness(true);
            message = messageRepository.save(message);
        }
        return message;
    }

    public List<MessageAttachment> getAttachments(Long messageId) {
        // findById already enforces the business/tenant check.
        findById(messageId);
        return attachmentRepository.findByMessageId(messageId);
    }

    public Message create(String subject, String body, List<MultipartFile> files) {
        User sender = userRepository.findById(SecurityUtils.currentUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Business business = businessRepository.findById(SecurityUtils.currentBusinessId())
                .orElseThrow(() -> new ResourceNotFoundException("Business not found"));

        boolean isSuperAdmin = SecurityUtils.isSuperAdmin();
        Message message = Message.builder()
                .subject(subject)
                .body(body)
                .sender(sender)
                .business(business)
                // A message is automatically "read" on the side that wrote it —
                // you don't need a notification about your own message.
                .readBySuperAdmin(isSuperAdmin)
                .readByBusiness(!isSuperAdmin)
                .build();
        message = messageRepository.save(message);

        int attachmentCount = 0;
        if (files != null) {
            for (MultipartFile file : files) {
                if (file == null || file.isEmpty()) continue;
                String path = fileStorageService.store(file);
                MessageAttachment attachment = MessageAttachment.builder()
                        .message(message)
                        .fileName(file.getOriginalFilename())
                        .filePath(path)
                        .fileSizeBytes(file.getSize())
                        .build();
                attachmentRepository.save(attachment);
                attachmentCount++;
            }
        }

        // Notify whoever DIDN'T write this message — the platform side if a
        // municipality posted, or every enabled user at this municipality if
        // the platform posted. Async + fail-safe: never blocks or fails the
        // request that's creating the message.
        List<User> recipients = isSuperAdmin
                ? userRepository.findByBusinessIdAndEnabledTrueAndEmailNotificationsEnabledTrue(business.getId())
                : userRepository.findByRoleAndEnabledTrueAndEmailNotificationsEnabledTrue(Role.SUPER_ADMIN);
        emailService.notifyNewMessage(message, recipients, attachmentCount);

        return message;
    }

    /** Side-effect-free count for badges — checking it must never clear it. */
    public long unreadCount() {
        Long businessId = SecurityUtils.currentBusinessId();
        return SecurityUtils.isSuperAdmin()
                ? messageRepository.countByBusinessIdAndReadBySuperAdminFalse(businessId)
                : messageRepository.countByBusinessIdAndReadByBusinessFalse(businessId);
    }

    /** Resolves an attachment while enforcing that it belongs to a message in the caller's own business. */
    public MessageAttachment resolveAttachmentForDownload(Long messageId, Long attachmentId) {
        findById(messageId); // enforces business scoping, 404s otherwise
        return attachmentRepository.findByIdAndMessageId(attachmentId, messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found"));
    }

    private void markThreadReadForCaller(Long businessId) {
        if (SecurityUtils.isSuperAdmin()) {
            List<Message> unread = messageRepository.findByBusinessIdAndReadBySuperAdminFalse(businessId);
            unread.forEach(m -> m.setReadBySuperAdmin(true));
            if (!unread.isEmpty()) {
                messageRepository.saveAll(unread);
            }
        } else {
            List<Message> unread = messageRepository.findByBusinessIdAndReadByBusinessFalse(businessId);
            unread.forEach(m -> m.setReadByBusiness(true));
            if (!unread.isEmpty()) {
                messageRepository.saveAll(unread);
            }
        }
    }
}
