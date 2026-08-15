package com.philadelphia.inventory.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.philadelphia.inventory.entity.Artifact;
import com.philadelphia.inventory.entity.ArtifactPhoto;
import com.philadelphia.inventory.entity.User;
import com.philadelphia.inventory.repository.ArtifactPhotoRepository;

@Service
public class ArtifactPhotoService {

    private final ArtifactPhotoRepository artifactPhotoRepository;
    private final ArtifactChangeLogService changeLogService;
    private final FileStorageService fileStorageService;

    public ArtifactPhotoService(
            ArtifactPhotoRepository artifactPhotoRepository,
            ArtifactChangeLogService changeLogService,
            FileStorageService fileStorageService
    ) {
        this.artifactPhotoRepository = artifactPhotoRepository;
        this.changeLogService = changeLogService;
        this.fileStorageService = fileStorageService;
    }

    @Transactional(readOnly = true)
    public List<ArtifactPhoto> getActivePhotos(Long artifactId) {
        return artifactPhotoRepository
                .findActivePhotosWithRelations(artifactId);
    }

    @Transactional(readOnly = true)
    public List<ArtifactPhoto> getDeletedPhotos(Long artifactId) {
        return artifactPhotoRepository
                .findDeletedPhotosWithRelations(artifactId);
    }

    @Transactional(readOnly = true)
    public ArtifactPhoto getPhoto(Long photoId) {
        return artifactPhotoRepository
                .findByIdWithRelations(photoId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Photo not found."
                        )
                );
    }

    @Transactional
    public ArtifactPhoto uploadPhoto(
            Artifact artifact,
            String photoNo,
            MultipartFile file,
            User uploadedBy
    ) {

        String storagePath =
                fileStorageService.storeArtifactPhoto(
                        artifact.getId(),
                        file
                );

        ArtifactPhoto photo = new ArtifactPhoto();

        photo.setArtifact(artifact);
        photo.setPhotoNo(photoNo);
        photo.setFileName(file.getOriginalFilename());
        photo.setContentType(file.getContentType());
        photo.setStoragePath(storagePath);
        photo.setUploadedBy(uploadedBy);
        photo.setUploadedAt(LocalDateTime.now());
        photo.setDeleted(false);

        ArtifactPhoto savedPhoto =
                artifactPhotoRepository.save(photo);

        changeLogService.recordPhotoAdded(
                artifact,
                uploadedBy,
                photoNo
        );

        return savedPhoto;
    }

    @Transactional(readOnly = true)
    public byte[] loadPhotoFile(Long photoId) {

        ArtifactPhoto photo =
                artifactPhotoRepository
                        .findByIdWithRelations(photoId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Photo not found."
                                )
                        );

        if (photo.isDeleted()) {
            throw new IllegalArgumentException(
                    "Photo is deleted."
            );
        }

        return fileStorageService.loadFile(
                photo.getStoragePath()
        );
    }

    @Transactional
    public ArtifactPhoto softDeletePhoto(
            Long photoId,
            User deletedBy
    ) {

        ArtifactPhoto photo =
                artifactPhotoRepository
                        .findByIdWithRelations(photoId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Photo not found."
                                )
                        );

        if (photo.isDeleted()) {
            throw new IllegalArgumentException(
                    "Photo is already deleted."
            );
        }

        photo.setDeleted(true);
        photo.setDeletedBy(deletedBy);
        photo.setDeletedAt(LocalDateTime.now());

        ArtifactPhoto savedPhoto =
                artifactPhotoRepository.save(photo);

        changeLogService.recordPhotoRemoved(
                photo.getArtifact(),
                deletedBy,
                photo.getPhotoNo()
        );

        return savedPhoto;
    }

    @Transactional
    public ArtifactPhoto restorePhoto(Long photoId) {

        ArtifactPhoto photo =
                artifactPhotoRepository
                        .findByIdWithRelations(photoId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Photo not found."
                                )
                        );

        if (!photo.isDeleted()) {
            throw new IllegalArgumentException(
                    "Photo is not deleted."
            );
        }

        photo.setDeleted(false);
        photo.setDeletedBy(null);
        photo.setDeletedAt(null);

        return artifactPhotoRepository.save(photo);
    }
}