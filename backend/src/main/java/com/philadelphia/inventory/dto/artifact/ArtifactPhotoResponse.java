package com.philadelphia.inventory.dto.artifact;

import java.time.LocalDateTime;

public record ArtifactPhotoResponse(
        Long id,
        Long artifactId,
        String photoNo,
        String fileName,
        String contentType,
        Long uploadedById,
        String uploadedByName,
        LocalDateTime uploadedAt,
        boolean deleted,
        Long deletedById,
        String deletedByName,
        LocalDateTime deletedAt
) {
}