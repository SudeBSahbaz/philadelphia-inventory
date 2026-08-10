package com.philadelphia.inventory.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.philadelphia.inventory.entity.enums.ArtifactVisibility;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "artifacts",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_artifact_code",
                        columnNames = "artifact_code"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Artifact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "artifact_code", nullable = false, unique = true, length = 150)
    private String artifactCode;

    @Column(name = "artifact_type")
    private String type;

    @Column(name = "form_no")
    private String formNo;

    @Column(name = "inventory_no")
    private String inventoryNo;

    @Column(name = "study_no")
    private String studyNo;

    @Column(name = "bag_no")
    private String bagNo;

    @Column(name = "box_no")
    private String boxNo;

    @Column(name = "depth")
    private String depth;

    @Column(name = "box")
    private String box;

    @Column(name = "find_location")
    private String findLocation;

    @Column(name = "locality")
    private String locality;

    @Column(name = "sector")
    private String sector;

    @Column(name = "find_date")
    private LocalDate findDate;

    @Column(name = "find_year")
    private Integer findYear;

    @Column(name = "area")
    private String area;

    @Column(name = "artifact_form")
    private String form;

    @Column(name = "decoration_type")
    private String decorationType;

    @Column(name = "paste_structure")
    private String pasteStructure;

    @Column(name = "firing")
    private String firing;

    @Column(name = "technique")
    private String technique;

    @Column(name = "temper")
    private String temper;

    @Column(name = "temper_amount")
    private String temperAmount;

    @Column(name = "slip_structure")
    private String slipStructure;

    @Column(name = "angle")
    private String angle;

    @Column(name = "period")
    private String period;

    @Column(name = "kind")
    private String kind;

    @Column(name = "munsell")
    private String munsell;

    @Column(name = "diameter")
    private String diameter;

    @Column(name = "weight")
    private String weight;

    @Column(name = "length")
    private String length;

    @Column(name = "width")
    private String width;

    @Column(name = "thickness")
    private String thickness;

    @Column(name = "drawing_no")
    private String drawingNo;

    @Column(name = "preserved_part")
    private String preservedPart;

    @Column(name = "material")
    private String material;

    @Column(name = "production_place")
    private String productionPlace;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String bibliography;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ArtifactVisibility visibility;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "updated_by", nullable = false)
    private User updatedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deleted_by")
    private User deletedBy;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}