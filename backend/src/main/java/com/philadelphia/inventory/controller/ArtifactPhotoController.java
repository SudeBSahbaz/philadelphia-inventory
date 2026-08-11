package com.philadelphia.inventory.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.philadelphia.inventory.entity.ArtifactPhoto;
import com.philadelphia.inventory.service.ArtifactPhotoService;

@RestController
@RequestMapping("/api/artifacts")
public class ArtifactPhotoController {

    private final ArtifactPhotoService artifactPhotoService;

    public ArtifactPhotoController(
            ArtifactPhotoService artifactPhotoService
    ) {
        this.artifactPhotoService = artifactPhotoService;
    }

    // Bir buluntuya ait aktif fotoğrafları getir
    @GetMapping("/{artifactId}/photos")
    public ResponseEntity<List<ArtifactPhoto>> getArtifactPhotos(
            @PathVariable Long artifactId
    ) {
        return ResponseEntity.ok(
                artifactPhotoService.getActivePhotos(artifactId)
        );
    }
}