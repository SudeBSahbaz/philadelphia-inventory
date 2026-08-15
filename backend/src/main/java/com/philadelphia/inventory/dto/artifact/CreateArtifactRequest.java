package com.philadelphia.inventory.dto.artifact;

import java.time.LocalDate;

import com.philadelphia.inventory.entity.enums.ArtifactVisibility;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateArtifactRequest {

    // Sistemde arama ve oluşturma için kullanılan benzersiz kod
    @NotBlank(message = "Artifact code is required.")
    @Size(max = 100, message = "Artifact code must be at most 100 characters.")
    private String artifactCode;

    // Form alanları
    @Size(max = 255, message = "Type must be at most 255 characters.")
    private String type;

    @Size(max = 100, message = "Form number must be at most 100 characters.")
    private String formNo;

    @Size(max = 100, message = "Inventory number must be at most 100 characters.")
    private String inventoryNo;

    @Size(max = 100, message = "Study number must be at most 100 characters.")
    private String studyNo;

    @Size(max = 100, message = "Bag number must be at most 100 characters.")
    private String bagNo;

    @Size(max = 100, message = "Box number must be at most 100 characters.")
    private String boxNo;

    @Size(max = 100, message = "Depth must be at most 100 characters.")
    private String depth;

    @Size(max = 100, message = "Box must be at most 100 characters.")
    private String box;

    @Size(max = 255, message = "Find location must be at most 255 characters.")
    private String findLocation;

    @Size(max = 255, message = "Locality must be at most 255 characters.")
    private String locality;

    @Size(max = 100, message = "Sector must be at most 100 characters.")
    private String sector;

    private LocalDate findDate;

    private Integer findYear;

    @Size(max = 100, message = "Area must be at most 100 characters.")
    private String area;

    @Size(max = 255, message = "Form must be at most 255 characters.")
    private String form;

    @Size(max = 255, message = "Decoration type must be at most 255 characters.")
    private String decorationType;

    @Size(max = 255, message = "Paste structure must be at most 255 characters.")
    private String pasteStructure;

    @Size(max = 255, message = "Firing must be at most 255 characters.")
    private String firing;

    @Size(max = 255, message = "Technique must be at most 255 characters.")
    private String technique;

    @Size(max = 255, message = "Temper must be at most 255 characters.")
    private String temper;

    @Size(max = 255, message = "Temper amount must be at most 255 characters.")
    private String temperAmount;

    @Size(max = 255, message = "Slip structure must be at most 255 characters.")
    private String slipStructure;

    @Size(max = 100, message = "Angle must be at most 100 characters.")
    private String angle;

    @Size(max = 255, message = "Period must be at most 255 characters.")
    private String period;

    @Size(max = 255, message = "Kind must be at most 255 characters.")
    private String kind;

    @Size(max = 100, message = "Munsell must be at most 100 characters.")
    private String munsell;

    @Size(max = 100, message = "Diameter must be at most 100 characters.")
    private String diameter;

    @Size(max = 100, message = "Weight must be at most 100 characters.")
    private String weight;

    @Size(max = 100, message = "Length must be at most 100 characters.")
    private String length;

    @Size(max = 100, message = "Width must be at most 100 characters.")
    private String width;

    @Size(max = 100, message = "Thickness must be at most 100 characters.")
    private String thickness;

    @Size(max = 100, message = "Drawing number must be at most 100 characters.")
    private String drawingNo;

    @Size(max = 255, message = "Preserved part must be at most 255 characters.")
    private String preservedPart;

    @Size(max = 255, message = "Material must be at most 255 characters.")
    private String material;

    @Size(max = 255, message = "Production place must be at most 255 characters.")
    private String productionPlace;

    @Size(max = 2000, message = "Description must be at most 2000 characters.")
    private String description;

    @Size(max = 2000, message = "Bibliography must be at most 2000 characters.")
    private String bibliography;

    // PUBLIC veya PRIVATE_FOR_CREW
    @NotNull(message = "Artifact visibility must be selected.")
    private ArtifactVisibility visibility;
}