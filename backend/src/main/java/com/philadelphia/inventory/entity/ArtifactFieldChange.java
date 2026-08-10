package com.philadelphia.inventory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "artifact_field_changes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArtifactFieldChange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "change_log_id",
            nullable = false,
            updatable = false
    )
    private ArtifactChangeLog changeLog;

    @Column(
            name = "field_name",
            nullable = false,
            updatable = false
    )
    private String fieldName;

    @Column(
            name = "old_value",
            columnDefinition = "TEXT",
            updatable = false
    )
    private String oldValue;

    @Column(
            name = "new_value",
            columnDefinition = "TEXT",
            updatable = false
    )
    private String newValue;

    public ArtifactFieldChange(
            ArtifactChangeLog changeLog,
            String fieldName,
            String oldValue,
            String newValue
    ) {
        this.changeLog = changeLog;
        this.fieldName = fieldName;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
}