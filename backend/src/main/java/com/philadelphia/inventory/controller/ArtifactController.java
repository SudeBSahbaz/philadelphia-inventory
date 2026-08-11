package com.philadelphia.inventory.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.philadelphia.inventory.dto.artifact.ArtifactListItemResponse;
import com.philadelphia.inventory.dto.artifact.ArtifactResponse;
import com.philadelphia.inventory.entity.Artifact;
import com.philadelphia.inventory.service.ArtifactService;

@RestController
@RequestMapping("/api/artifacts")
public class ArtifactController {

    private final ArtifactService artifactService;

    public ArtifactController(ArtifactService artifactService) {
        this.artifactService = artifactService;
    }

    // Aktif buluntuların tamamını listele
    @GetMapping
    public ResponseEntity<List<ArtifactListItemResponse>> getAllArtifacts() {

        List<ArtifactListItemResponse> response =
                artifactService.getAllActiveArtifacts()
                        .stream()
                        .map(this::toListItemResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    // Buluntu Kodu ile buluntu ara
    @GetMapping("/code/{artifactCode}")
    public ResponseEntity<ArtifactResponse> getByArtifactCode(
            @PathVariable String artifactCode
    ) {
        return artifactService.getByArtifactCode(artifactCode)
                .map(this::toArtifactResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Silinmiş buluntuları listele
    @GetMapping("/deleted")
    public ResponseEntity<List<ArtifactListItemResponse>> getDeletedArtifacts() {

        List<ArtifactListItemResponse> response =
                artifactService.getAllDeletedArtifacts()
                        .stream()
                        .map(this::toListItemResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    // PUBLIC buluntuları listele
    @GetMapping("/public")
    public ResponseEntity<List<ArtifactListItemResponse>> getPublicArtifacts() {

        List<ArtifactListItemResponse> response =
                artifactService.getPublicArtifacts()
                        .stream()
                        .map(this::toListItemResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    private ArtifactListItemResponse toListItemResponse(
            Artifact artifact
    ) {
        return new ArtifactListItemResponse(
                artifact.getId(),
                artifact.getArtifactCode(),
                artifact.getFormNo(),
                artifact.getType(),
                artifact.getFindLocation(),
                artifact.getSector(),
                artifact.getFindDate(),
                artifact.getPeriod(),
                artifact.getVisibility(),
                getUserName(artifact.getUpdatedBy()),
                artifact.getUpdatedAt(),
                artifact.isDeleted()
        );
    }

    private ArtifactResponse toArtifactResponse(
            Artifact artifact
    ) {
        return new ArtifactResponse(
                artifact.getId(),
                artifact.getArtifactCode(),
                artifact.getType(),
                artifact.getFormNo(),
                artifact.getInventoryNo(),
                artifact.getStudyNo(),
                artifact.getBagNo(),
                artifact.getBoxNo(),
                artifact.getDepth(),
                artifact.getBox(),
                artifact.getFindLocation(),
                artifact.getLocality(),
                artifact.getSector(),
                artifact.getFindDate(),
                artifact.getFindYear(),
                artifact.getArea(),
                artifact.getForm(),
                artifact.getDecorationType(),
                artifact.getPasteStructure(),
                artifact.getFiring(),
                artifact.getTechnique(),
                artifact.getTemper(),
                artifact.getTemperAmount(),
                artifact.getSlipStructure(),
                artifact.getAngle(),
                artifact.getPeriod(),
                artifact.getKind(),
                artifact.getMunsell(),
                artifact.getDiameter(),
                artifact.getWeight(),
                artifact.getLength(),
                artifact.getWidth(),
                artifact.getThickness(),
                artifact.getDrawingNo(),
                artifact.getPreservedPart(),
                artifact.getMaterial(),
                artifact.getProductionPlace(),
                artifact.getDescription(),
                artifact.getBibliography(),
                artifact.getVisibility(),

                artifact.getCreatedBy() != null
                        ? artifact.getCreatedBy().getId()
                        : null,

                getUserName(artifact.getCreatedBy()),

                artifact.getCreatedAt(),

                artifact.getUpdatedBy() != null
                        ? artifact.getUpdatedBy().getId()
                        : null,

                getUserName(artifact.getUpdatedBy()),

                artifact.getUpdatedAt(),
                artifact.isDeleted()
        );
    }

    private String getUserName(
            com.philadelphia.inventory.entity.User user
    ) {
        if (user == null) {
            return null;
        }

        return user.getFirstName() + " " + user.getLastName();
    }
}