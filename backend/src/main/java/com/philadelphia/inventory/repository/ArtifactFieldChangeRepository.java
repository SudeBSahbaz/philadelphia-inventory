package com.philadelphia.inventory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.philadelphia.inventory.entity.ArtifactFieldChange;

public interface ArtifactFieldChangeRepository
        extends JpaRepository<ArtifactFieldChange, Long> {

    List<ArtifactFieldChange> findAllByChangeLogId(Long changeLogId);

}