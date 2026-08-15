package com.philadelphia.inventory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.philadelphia.inventory.entity.ArtifactChangeLog;

public interface ArtifactChangeLogRepository
        extends JpaRepository<ArtifactChangeLog, Long> {

    @EntityGraph(
            attributePaths = {
                    "changedBy",
                    "fieldChanges"
            }
    )
    List<ArtifactChangeLog> findAllByArtifactIdOrderByChangedAtDesc(
            Long artifactId
    );
}