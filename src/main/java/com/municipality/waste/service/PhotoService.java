package com.municipality.waste.service;

import com.municipality.waste.entity.Business;
import com.municipality.waste.entity.Photo;
import com.municipality.waste.exception.ResourceNotFoundException;
import com.municipality.waste.repository.BusinessRepository;
import com.municipality.waste.repository.PhotoRepository;
import com.municipality.waste.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final FileStorageService fileStorageService;
    private final BusinessRepository businessRepository;

    public List<Photo> findAll() {
        return photoRepository.findByBusinessId(SecurityUtils.currentBusinessId());
    }

    public List<Photo> findByRelatedEntity(String type, Long id) {
        return photoRepository.findByRelatedEntityTypeAndRelatedEntityIdAndBusinessId(
                type, id, SecurityUtils.currentBusinessId());
    }

    public Photo findById(Long id) {
        return photoRepository.findByIdAndBusinessId(id, SecurityUtils.currentBusinessId())
                .orElseThrow(() -> new ResourceNotFoundException("Photo not found with id: " + id));
    }

    public Photo upload(MultipartFile file, String relatedEntityType, Long relatedEntityId,
                         String caption, String uploadedBy) {
        String path = fileStorageService.store(file);

        Photo photo = Photo.builder()
                .relatedEntityType(relatedEntityType)
                .relatedEntityId(relatedEntityId)
                .filePath(path)
                .fileName(file.getOriginalFilename())
                .caption(caption)
                .uploadedBy(uploadedBy)
                .business(currentBusiness())
                .build();

        return photoRepository.save(photo);
    }

    public void delete(Long id) {
        photoRepository.delete(findById(id));
    }

    private Business currentBusiness() {
        return businessRepository.findById(SecurityUtils.currentBusinessId())
                .orElseThrow(() -> new ResourceNotFoundException("Business not found"));
    }
}
