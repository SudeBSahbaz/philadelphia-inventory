package com.philadelphia.inventory.entity;

import com.philadelphia.inventory.entity.enums.ChangeType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "artifact_change_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArtifactChangeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "artifact_id", nullable = false, updatable = false)
    private Artifact artifact;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "changed_by", nullable = false, updatable = false)
    private User changedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "change_type", nullable = false, updatable = false)
    private ChangeType changeType;

    @Column(name = "changed_at", nullable = false, updatable = false)
    private LocalDateTime changedAt;

    @OneToMany(
            mappedBy = "changeLog",
            cascade = CascadeType.ALL,
            orphanRemoval = false
    )
    private List<ArtifactFieldChange> fieldChanges = new ArrayList<>();

    public ArtifactChangeLog(
            Artifact artifact,
            User changedBy,
            ChangeType changeType
    ) {
        this.artifact = artifact;
        this.changedBy = changedBy;
        this.changeType = changeType;
        this.changedAt = LocalDateTime.now();
    }

    public void addFieldChange(
            String fieldName,
            String oldValue,
            String newValue
    ) {
        ArtifactFieldChange fieldChange =
                new ArtifactFieldChange(
                        this,
                        fieldName,
                        oldValue,
                        newValue
                );

        fieldChanges.add(fieldChange);
    }
}