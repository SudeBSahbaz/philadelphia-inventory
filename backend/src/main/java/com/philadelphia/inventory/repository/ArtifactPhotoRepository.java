package com.philadelphia.inventory.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.philadelphia.inventory.entity.ArtifactPhoto;

public interface ArtifactPhotoRepository
        extends JpaRepository<ArtifactPhoto, Long> {

    @Query("""
            SELECT p
            FROM ArtifactPhoto p
            JOIN FETCH p.artifact
            JOIN FETCH p.uploadedBy
            LEFT JOIN FETCH p.deletedBy
            WHERE p.artifact.id = :artifactId
              AND p.deleted = false
            ORDER BY p.id ASC
            """)
    List<ArtifactPhoto> findActivePhotosWithRelations(
            @Param("artifactId") Long artifactId
    );

    @Query("""
            SELECT p
            FROM ArtifactPhoto p
            JOIN FETCH p.artifact
            JOIN FETCH p.uploadedBy
            LEFT JOIN FETCH p.deletedBy
            WHERE p.artifact.id = :artifactId
              AND p.deleted = true
            ORDER BY p.id ASC
            """)
    List<ArtifactPhoto> findDeletedPhotosWithRelations(
            @Param("artifactId") Long artifactId
    );

    @Query("""
            SELECT p
            FROM ArtifactPhoto p
            JOIN FETCH p.artifact
            JOIN FETCH p.uploadedBy
            LEFT JOIN FETCH p.deletedBy
            WHERE p.id = :photoId
            """)
    Optional<ArtifactPhoto> findByIdWithRelations(
            @Param("photoId") Long photoId
    );
}