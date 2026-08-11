package com.philadelphia.inventory.dto.artifact;

import java.time.LocalDate;

import com.philadelphia.inventory.entity.enums.ArtifactVisibility;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateArtifactRequest {

    private String artifactCode;

    private String type;
    private String formNo;
    private String inventoryNo;
    private String studyNo;
    private String bagNo;
    private String boxNo;
    private String depth;
    private String box;
    private String findLocation;
    private String locality;
    private String sector;
    private LocalDate findDate;
    private Integer findYear;
    private String area;
    private String form;
    private String decorationType;
    private String pasteStructure;
    private String firing;
    private String technique;
    private String temper;
    private String temperAmount;
    private String slipStructure;
    private String angle;
    private String period;
    private String kind;
    private String munsell;
    private String diameter;
    private String weight;
    private String length;
    private String width;
    private String thickness;
    private String drawingNo;
    private String preservedPart;
    private String material;
    private String productionPlace;
    private String description;
    private String bibliography;

    private ArtifactVisibility visibility;
}