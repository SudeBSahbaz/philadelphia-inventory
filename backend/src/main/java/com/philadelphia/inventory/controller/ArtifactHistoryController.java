package com.philadelphia.inventory.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.philadelphia.inventory.dto.artifact.ArtifactHistoryResponse;
import com.philadelphia.inventory.entity.ArtifactChangeLog;
import com.philadelphia.inventory.entity.ArtifactFieldChange;
import com.philadelphia.inventory.service.ArtifactChangeLogService;

@RestController
@RequestMapping("/api/artifacts")
public class ArtifactHistoryController {

    private final ArtifactChangeLogService changeLogService;

    public ArtifactHistoryController(
            ArtifactChangeLogService changeLogService
    ) {
        this.changeLogService = changeLogService;
    }

    @GetMapping("/{artifactId}/history")
    public ResponseEntity<List<ArtifactHistoryResponse>> getArtifactHistory(
            @PathVariable Long artifactId
    ) {

        List<ArtifactHistoryResponse> response =
                changeLogService.getArtifactHistory(artifactId)
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    private ArtifactHistoryResponse toResponse(
            ArtifactChangeLog log
    ) {
        List<ArtifactHistoryResponse.FieldChangeResponse> fieldChanges =
                log.getFieldChanges()
                        .stream()
                        .map(this::toFieldChangeResponse)
                        .toList();

        return new ArtifactHistoryResponse(
                log.getId(),
                log.getChangeType(),

                log.getChangedBy() != null
                        ? log.getChangedBy().getId()
                        : null,

                getUserName(log),

                log.getChangedAt(),
                fieldChanges
        );
    }

    private ArtifactHistoryResponse.FieldChangeResponse toFieldChangeResponse(
            ArtifactFieldChange fieldChange
    ) {
        return new ArtifactHistoryResponse.FieldChangeResponse(
                fieldChange.getFieldName(),
                fieldChange.getOldValue(),
                fieldChange.getNewValue()
        );
    }

    private String getUserName(ArtifactChangeLog log) {
        if (log.getChangedBy() == null) {
            return null;
        }

        return log.getChangedBy().getFirstName()
                + " "
                + log.getChangedBy().getLastName();
    }
}