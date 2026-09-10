package com.municipality.waste.controller;

import com.municipality.waste.entity.Message;
import com.municipality.waste.entity.MessageAttachment;
import com.municipality.waste.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * The communication + document channel between the platform (SUPER_ADMIN)
 * and one municipality (Business). Available to ADMIN/MANAGER within their
 * own business, and to SUPER_ADMIN while "viewing as" a chosen municipality
 * (enforced by SecurityUtils via the X-Business-Id header).
 */
@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MANAGER','SUPER_ADMIN')")
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    public ResponseEntity<List<Message>> getAll() {
        return ResponseEntity.ok(messageService.findAll());
    }

    /**
     * Side-effect-free — used by the sidebar badge and polling, so simply
     * checking the count never clears it (only actually opening the
     * Messages page, which calls getAll() above, does that).
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount() {
        return ResponseEntity.ok(messageService.unreadCount());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Message> getById(@PathVariable Long id) {
        return ResponseEntity.ok(messageService.findById(id));
    }

    @GetMapping("/{id}/attachments")
    public ResponseEntity<List<MessageAttachment>> getAttachments(@PathVariable Long id) {
        return ResponseEntity.ok(messageService.getAttachments(id));
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Message> create(@RequestParam String subject,
                                           @RequestParam(required = false) String body,
                                           @RequestParam(required = false) List<MultipartFile> files) {
        return ResponseEntity.ok(messageService.create(subject, body, files));
    }

    @GetMapping("/{messageId}/attachments/{attachmentId}/download")
    public ResponseEntity<Resource> download(@PathVariable Long messageId, @PathVariable Long attachmentId) {
        MessageAttachment attachment = messageService.resolveAttachmentForDownload(messageId, attachmentId);

        File file = new File(attachment.getFilePath());
        Resource resource = new FileSystemResource(file);

        String encodedName = URLEncoder.encode(attachment.getFileName(), StandardCharsets.UTF_8).replace("+", "%20");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedName)
                .body(resource);
    }
}
