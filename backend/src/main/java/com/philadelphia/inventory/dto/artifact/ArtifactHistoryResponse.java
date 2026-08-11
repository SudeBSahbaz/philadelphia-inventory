package com.philadelphia.inventory.dto.artifact;

import java.time.LocalDateTime;
import java.util.List;

import com.philadelphia.inventory.entity.enums.ChangeType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ArtifactHistoryResponse {

    private Long id;

    private ChangeType changeType;

    private Long changedById;

    private String changedByName;

    private LocalDateTime changedAt;

    private List<FieldChangeResponse> fieldChanges;

    @Getter
    @AllArgsConstructor
    public static class FieldChangeResponse {

        private String fieldName;

        private String oldValue;

        private String newValue;
    }
}