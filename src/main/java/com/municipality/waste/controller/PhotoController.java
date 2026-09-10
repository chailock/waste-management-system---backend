package com.municipality.waste.controller;

import com.municipality.waste.entity.Photo;
import com.municipality.waste.security.UserPrincipal;
import com.municipality.waste.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/photos")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
public class PhotoController {

    private final PhotoService photoService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SUPER_ADMIN')")
    public ResponseEntity<List<Photo>> getAll() {
        return ResponseEntity.ok(photoService.findAll());
    }

    @GetMapping("/by-entity")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SUPER_ADMIN')")
    public ResponseEntity<List<Photo>> getByEntity(@RequestParam String type, @RequestParam Long id) {
        return ResponseEntity.ok(photoService.findByRelatedEntity(type, id));
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Photo> upload(@RequestParam("file") MultipartFile file,
                                         @RequestParam String relatedEntityType,
                                         @RequestParam Long relatedEntityId,
                                         @RequestParam(required = false) String caption,
                                         @AuthenticationPrincipal UserPrincipal principal) {
        String uploadedBy = principal != null ? principal.getUsername() : "unknown";
        return ResponseEntity.ok(photoService.upload(file, relatedEntityType, relatedEntityId, caption, uploadedBy));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        photoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
