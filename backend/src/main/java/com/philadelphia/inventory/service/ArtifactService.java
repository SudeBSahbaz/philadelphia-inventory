package com.philadelphia.inventory.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.philadelphia.inventory.entity.Artifact;
import com.philadelphia.inventory.entity.User;
import com.philadelphia.inventory.entity.enums.ArtifactVisibility;
import com.philadelphia.inventory.repository.ArtifactRepository;

@Service
public class ArtifactService {

    private final ArtifactRepository artifactRepository;
    private final ArtifactChangeLogService changeLogService;

    public ArtifactService(
            ArtifactRepository artifactRepository,
            ArtifactChangeLogService changeLogService
    ) {
        this.artifactRepository = artifactRepository;
        this.changeLogService = changeLogService;
    }

    public List<Artifact> getAllActiveArtifacts() {
        return artifactRepository.findAllByDeletedFalse();
    }

    public List<Artifact> getAllDeletedArtifacts() {
        return artifactRepository.findAllByDeletedTrue();
    }

    public List<Artifact> getPublicArtifacts() {
        return artifactRepository.findAllByVisibilityAndDeletedFalse(
                ArtifactVisibility.PUBLIC
        );
    }

    public Optional<Artifact> getByArtifactCode(String artifactCode) {
        return artifactRepository.findByArtifactCodeAndDeletedFalse(
                artifactCode
        );
    }

    public Artifact createArtifact(
            Artifact artifact,
            User createdBy
    ) {
        if (artifactRepository.existsByArtifactCodeAndDeletedFalse(
                artifact.getArtifactCode()
        )) {
            throw new IllegalArgumentException(
                    "An artifact with this code already exists."
            );
        }

        if (artifact.getVisibility() == null) {
            throw new IllegalArgumentException(
                    "Artifact visibility must be selected."
            );
        }

        artifact.setCreatedBy(createdBy);
        artifact.setUpdatedBy(createdBy);

        LocalDateTime now = LocalDateTime.now();
        artifact.setCreatedAt(now);
        artifact.setUpdatedAt(now);

        artifact.setDeleted(false);

        Artifact savedArtifact = artifactRepository.save(artifact);

        changeLogService.recordCreated(savedArtifact, createdBy);

        return savedArtifact;
    }

    public Artifact updateArtifact(
            Long artifactId,
            Artifact updatedArtifact,
            User updatedBy
    ) {
        Artifact existingArtifact = artifactRepository.findById(artifactId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Artifact not found.")
                );

        if (existingArtifact.isDeleted()) {
            throw new IllegalArgumentException(
                    "Deleted artifacts cannot be edited."
            );
        }

        String newArtifactCode = updatedArtifact.getArtifactCode();

        if (!existingArtifact.getArtifactCode().equals(newArtifactCode)
                && artifactRepository.existsByArtifactCodeAndDeletedFalse(
                        newArtifactCode
                )) {
            throw new IllegalArgumentException(
                    "An artifact with this code already exists."
            );
        }

        changeLogService.recordUpdated(
                existingArtifact,
                updatedArtifact,
                updatedBy
        );

        updatedArtifact.setId(existingArtifact.getId());
        updatedArtifact.setCreatedBy(existingArtifact.getCreatedBy());
        updatedArtifact.setCreatedAt(existingArtifact.getCreatedAt());

        updatedArtifact.setUpdatedBy(updatedBy);
        updatedArtifact.setUpdatedAt(LocalDateTime.now());

        updatedArtifact.setDeleted(false);
        updatedArtifact.setDeletedBy(null);
        updatedArtifact.setDeletedAt(null);

        return artifactRepository.save(updatedArtifact);
    }

    public Artifact softDelete(
            Long artifactId,
            User deletedBy
    ) {
        Artifact artifact = artifactRepository.findById(artifactId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Artifact not found.")
                );

        if (artifact.isDeleted()) {
            throw new IllegalArgumentException(
                    "Artifact is already deleted."
            );
        }

        artifact.setDeleted(true);
        artifact.setDeletedBy(deletedBy);
        artifact.setDeletedAt(LocalDateTime.now());
        artifact.setUpdatedBy(deletedBy);
        artifact.setUpdatedAt(LocalDateTime.now());

        Artifact savedArtifact = artifactRepository.save(artifact);

        changeLogService.recordDeleted(savedArtifact, deletedBy);

        return savedArtifact;
    }

    public Artifact restore(
            Long artifactId,
            User restoredBy
    ) {
        Artifact artifact = artifactRepository.findById(artifactId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Artifact not found.")
                );

        if (!artifact.isDeleted()) {
            throw new IllegalArgumentException(
                    "Artifact is not deleted."
            );
        }

        artifact.setDeleted(false);
        artifact.setDeletedBy(null);
        artifact.setDeletedAt(null);
        artifact.setUpdatedBy(restoredBy);
        artifact.setUpdatedAt(LocalDateTime.now());

        Artifact savedArtifact = artifactRepository.save(artifact);

        changeLogService.recordRestored(savedArtifact, restoredBy);

        return savedArtifact;
    }
}