package com.philadelphia.inventory.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.philadelphia.inventory.entity.Artifact;
import com.philadelphia.inventory.entity.enums.ArtifactVisibility;

public interface ArtifactRepository extends JpaRepository<Artifact, Long> {

    Optional<Artifact> findByArtifactCodeAndDeletedFalse(String artifactCode);

    boolean existsByArtifactCodeAndDeletedFalse(String artifactCode);

    List<Artifact> findAllByDeletedFalse();

    List<Artifact> findAllByDeletedTrue();

    List<Artifact> findAllByVisibilityAndDeletedFalse(
            ArtifactVisibility visibility
    );

}