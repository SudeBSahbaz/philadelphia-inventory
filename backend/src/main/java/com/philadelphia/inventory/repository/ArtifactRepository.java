package com.philadelphia.inventory.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.philadelphia.inventory.entity.Artifact;
import com.philadelphia.inventory.entity.enums.ArtifactVisibility;

public interface ArtifactRepository
        extends JpaRepository<Artifact, Long>,
                JpaSpecificationExecutor<Artifact> {

    // Aktif buluntuyu koduyla getir
    Optional<Artifact> findByArtifactCodeAndDeletedFalse(
            String artifactCode
    );

    // Aktif veya silinmiş herhangi bir kaydı koduyla getir
    Optional<Artifact> findByArtifactCode(
            String artifactCode
    );

    // Buluntu kodu aktif veya silinmiş herhangi bir kayıtta var mı?
    boolean existsByArtifactCode(
            String artifactCode
    );

    // Tüm aktif buluntular
    List<Artifact> findAllByDeletedFalse();

    // Tüm silinmiş buluntular
    List<Artifact> findAllByDeletedTrue();

    // Aktif PUBLIC buluntular
    List<Artifact> findAllByVisibilityAndDeletedFalse(
            ArtifactVisibility visibility
    );
}