package com.philadelphia.inventory.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.philadelphia.inventory.entity.Artifact;
import com.philadelphia.inventory.entity.ArtifactPhoto;
import com.philadelphia.inventory.entity.User;
import com.philadelphia.inventory.repository.ArtifactPhotoRepository;

@Service
public class ArtifactPhotoService {

    private final ArtifactPhotoRepository artifactPhotoRepository;
    private final ArtifactChangeLogService changeLogService;

    public ArtifactPhotoService(
            ArtifactPhotoRepository artifactPhotoRepository,
            ArtifactChangeLogService changeLogService
    ) {
        this.artifactPhotoRepository = artifactPhotoRepository;
        this.changeLogService = changeLogService;
    }

    public List<ArtifactPhoto> getActivePhotos(Long artifactId) {
        return artifactPhotoRepository
                .findAllByArtifactIdAndDeletedFalse(artifactId);
    }

    public List<ArtifactPhoto> getDeletedPhotos(Long artifactId) {
        return artifactPhotoRepository
                .findAllByArtifactIdAndDeletedTrue(artifactId);
    }

    public ArtifactPhoto addPhoto(
            Artifact artifact,
            String photoNo,
            String fileName,
            String contentType,
            String storagePath,
            User uploadedBy
    ) {
        ArtifactPhoto photo = new ArtifactPhoto();

        photo.setArtifact(artifact);
        photo.setPhotoNo(photoNo);
        photo.setFileName(fileName);
        photo.setContentType(contentType);
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

    public ArtifactPhoto softDeletePhoto(
            Long photoId,
            User deletedBy
    ) {
        ArtifactPhoto photo = artifactPhotoRepository.findById(photoId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Photo not found.")
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

    public ArtifactPhoto restorePhoto(Long photoId) {
        ArtifactPhoto photo = artifactPhotoRepository.findById(photoId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Photo not found.")
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