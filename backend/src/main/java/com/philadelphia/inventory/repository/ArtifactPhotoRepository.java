package com.philadelphia.inventory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.philadelphia.inventory.entity.ArtifactPhoto;

public interface ArtifactPhotoRepository extends JpaRepository<ArtifactPhoto, Long> {

    List<ArtifactPhoto> findAllByArtifactIdAndDeletedFalse(Long artifactId);

    List<ArtifactPhoto> findAllByArtifactIdAndDeletedTrue(Long artifactId);

}