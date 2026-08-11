package com.philadelphia.inventory.dto.artifact;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.philadelphia.inventory.entity.enums.ArtifactVisibility;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ArtifactListItemResponse {

    private Long id;

    private String artifactCode;

    private String formNo;

    private String type;

    private String findLocation;

    private String sector;

    private LocalDate findDate;

    private String period;

    private ArtifactVisibility visibility;

    private String updatedByName;

    private LocalDateTime updatedAt;

    private boolean deleted;
}