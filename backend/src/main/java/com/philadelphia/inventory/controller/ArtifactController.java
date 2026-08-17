package com.philadelphia.inventory.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.philadelphia.inventory.dto.artifact.ArtifactListItemResponse;
import com.philadelphia.inventory.dto.artifact.ArtifactResponse;
import com.philadelphia.inventory.dto.artifact.CreateArtifactRequest;
import com.philadelphia.inventory.dto.artifact.UpdateArtifactRequest;
import com.philadelphia.inventory.entity.Artifact;
import com.philadelphia.inventory.entity.User;
import com.philadelphia.inventory.entity.enums.ArtifactVisibility;
import com.philadelphia.inventory.entity.enums.Role;
import com.philadelphia.inventory.security.CustomUserPrincipal;
import com.philadelphia.inventory.service.ArtifactExcelService;
import com.philadelphia.inventory.service.ArtifactPdfService;
import com.philadelphia.inventory.service.ArtifactService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/artifacts")
public class ArtifactController {


    private final ArtifactService artifactService;

    private final ArtifactPdfService artifactPdfService;

    private final ArtifactExcelService artifactExcelService;


    public ArtifactController(
            ArtifactService artifactService,
            ArtifactPdfService artifactPdfService,
            ArtifactExcelService artifactExcelService
    ) {

        this.artifactService =
                artifactService;

        this.artifactPdfService =
                artifactPdfService;

        this.artifactExcelService =
                artifactExcelService;
    }


    // ----------------------------------------------------
    // LISTELEME
    // ----------------------------------------------------

    @GetMapping
    public ResponseEntity<List<ArtifactListItemResponse>>
    getAllArtifacts(
            Authentication authentication
    ) {

        CustomUserPrincipal principal =
                (CustomUserPrincipal)
                        authentication.getPrincipal();


        List<Artifact> artifacts;


        if (
                principal.getUser().getRole()
                        == Role.LOOKUP_USER
        ) {

            artifacts =
                    artifactService
                            .getPublicArtifacts();

        } else {

            artifacts =
                    artifactService
                            .getAllActiveArtifacts();
        }


        List<ArtifactListItemResponse> response =
                artifacts.stream()
                        .map(
                                this::toListItemResponse
                        )
                        .toList();


        return ResponseEntity.ok(
                response
        );
    }


    // ----------------------------------------------------
    // GELİŞMİŞ ARAMA
    // ----------------------------------------------------

    @GetMapping("/search")
    public ResponseEntity<List<ArtifactListItemResponse>>
    searchArtifacts(

            @RequestParam(required = false)
            String artifactCode,

            @RequestParam(required = false)
            String type,

            @RequestParam(required = false)
            String findLocation,

            @RequestParam(required = false)
            String locality,

            @RequestParam(required = false)
            String sector,

            @RequestParam(required = false)
            Integer findYear,

            @RequestParam(required = false)
            String period,

            @RequestParam(required = false)
            String material,

            @RequestParam(required = false)
            String form,

            @RequestParam(required = false)
            String decorationType,

            @RequestParam(required = false)
            String technique,

            @RequestParam(required = false)
            String munsell,

            Authentication authentication
    ) {

        CustomUserPrincipal principal =
                (CustomUserPrincipal)
                        authentication.getPrincipal();


        ArtifactVisibility visibility =
                null;


        // LOOKUP_USER yalnızca PUBLIC
        // buluntular arasında arama yapabilir.
        if (
                principal.getUser().getRole()
                        == Role.LOOKUP_USER
        ) {

            visibility =
                    ArtifactVisibility.PUBLIC;
        }


        List<Artifact> artifacts =
                artifactService.searchArtifacts(
                        artifactCode,
                        type,
                        findLocation,
                        locality,
                        sector,
                        findYear,
                        period,
                        material,
                        form,
                        decorationType,
                        technique,
                        munsell,
                        visibility
                );


        List<ArtifactListItemResponse> response =
                artifacts.stream()
                        .map(
                                this::toListItemResponse
                        )
                        .toList();


        return ResponseEntity.ok(
                response
        );
    }


    // ----------------------------------------------------
    // BULUNTU KODU İLE GETİR
    // ----------------------------------------------------

    @GetMapping("/code/{artifactCode}")
    public ResponseEntity<ArtifactResponse>
    getByArtifactCode(

            @PathVariable
            String artifactCode,

            Authentication authentication
    ) {

        CustomUserPrincipal principal =
                (CustomUserPrincipal)
                        authentication.getPrincipal();


        return artifactService
                .getByArtifactCode(
                        artifactCode
                )
                .filter(
                        artifact ->
                                canViewArtifact(
                                        artifact,
                                        principal.getUser()
                                )
                )
                .map(
                        this::toArtifactResponse
                )
                .map(
                        ResponseEntity::ok
                )
                .orElseGet(
                        () ->
                                ResponseEntity
                                        .notFound()
                                        .build()
                );
    }


    // ----------------------------------------------------
    // BULUNTU KODU DURUMU
    // ACTIVE / DELETED / AVAILABLE
    // ----------------------------------------------------

    @GetMapping("/code/{artifactCode}/status")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'CREW_MEMBER')"
    )
    public ResponseEntity<String>
    artifactCodeStatus(

            @PathVariable
            String artifactCode
    ) {

        return ResponseEntity.ok(
                artifactService
                        .getArtifactCodeStatus(
                                artifactCode
                        )
        );
    }


    // ----------------------------------------------------
    // TEK BULUNTU PDF DIŞA AKTAR
    // ADMIN + CREW_MEMBER
    // ----------------------------------------------------

    @GetMapping("/{artifactId}/pdf")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'CREW_MEMBER')"
    )
    public ResponseEntity<byte[]>
    exportArtifactPdf(

            @PathVariable
            Long artifactId,

            Authentication authentication
    ) {

        CustomUserPrincipal principal =
                (CustomUserPrincipal)
                        authentication.getPrincipal();


        Artifact artifact;


        try {

            artifact =
                    artifactService
                            .getArtifactEntityById(
                                    artifactId
                            );

        } catch (
                IllegalArgumentException exception
        ) {

            return ResponseEntity
                    .notFound()
                    .build();
        }


        if (
                !canViewArtifact(
                        artifact,
                        principal.getUser()
                )
        ) {

            return ResponseEntity
                    .status(403)
                    .build();
        }


        byte[] pdf =
                artifactPdfService
                        .generateArtifactPdf(
                                artifact
                        );


        String safeArtifactCode =
                sanitizeFileName(
                        artifact.getArtifactCode()
                );


        String fileName =
                safeArtifactCode
                        + ".pdf";


        return ResponseEntity
                .ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\""
                                + fileName
                                + "\""
                )
                .contentType(
                        MediaType.APPLICATION_PDF
                )
                .contentLength(
                        pdf.length
                )
                .body(
                        pdf
                );
    }


    // ----------------------------------------------------
    // TÜM AKTİF BULUNTULARI EXCEL OLARAK DIŞA AKTAR
    // ADMIN + CREW_MEMBER
    // ----------------------------------------------------

    @GetMapping("/excel")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'CREW_MEMBER')"
    )
    public ResponseEntity<byte[]>
    exportArtifactsExcel() {

        List<Artifact> artifacts =
                artifactService
                        .getAllActiveArtifacts();


        byte[] excel =
                artifactExcelService
                        .generateArtifactsExcel(
                                artifacts
                        );


        String fileName =
                "philadelphia-buluntular.xlsx";


        return ResponseEntity
                .ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\""
                                + fileName
                                + "\""
                )
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .contentLength(
                        excel.length
                )
                .body(
                        excel
                );
    }


 // ----------------------------------------------------
// SİLİNMİŞ BULUNTULAR
// ADMIN + CREW_MEMBER
// ----------------------------------------------------

@GetMapping("/deleted")
public ResponseEntity<List<ArtifactListItemResponse>>
getDeletedArtifacts(
        Authentication authentication
) {

    System.out.println(">>> GET /deleted METODUNA GIRILDI");

    CustomUserPrincipal principal =
            (CustomUserPrincipal)
                    authentication.getPrincipal();

    Role role =
            principal.getUser().getRole();

    System.out.println(">>> /deleted USER ROLE = " + role);
    System.out.println(">>> /deleted AUTHORITIES = " + authentication.getAuthorities());

    if (
            role != Role.ADMIN
                    &&
            role != Role.CREW_MEMBER
    ) {

        System.out.println(">>> /deleted 403 DONUYOR");

        return ResponseEntity
                .status(403)
                .build();
    }

    System.out.println(">>> /deleted YETKI BASARILI");

    List<ArtifactListItemResponse> response =
            artifactService
                    .getAllDeletedArtifacts()
                    .stream()
                    .map(
                            this::toListItemResponse
                    )
                    .toList();

    return ResponseEntity.ok(
            response
    );
}
    // ----------------------------------------------------
    // PUBLIC BULUNTULAR
    // ----------------------------------------------------

    @GetMapping("/public")
    public ResponseEntity<List<ArtifactListItemResponse>>
    getPublicArtifacts() {

        List<ArtifactListItemResponse> response =
                artifactService
                        .getPublicArtifacts()
                        .stream()
                        .map(
                                this::toListItemResponse
                        )
                        .toList();


        return ResponseEntity.ok(
                response
        );
    }


    // ----------------------------------------------------
    // YENİ BULUNTU OLUŞTUR
    // ADMIN + CREW_MEMBER
    // ----------------------------------------------------

    @PostMapping
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'CREW_MEMBER')"
    )
    public ResponseEntity<ArtifactResponse>
    createArtifact(

            @Valid
            @RequestBody
            CreateArtifactRequest request,

            Authentication authentication
    ) {

        CustomUserPrincipal principal =
                (CustomUserPrincipal)
                        authentication.getPrincipal();


        Artifact artifact =
                fromCreateRequest(
                        request
                );


        Artifact savedArtifact =
                artifactService
                        .createArtifact(
                                artifact,
                                principal.getUser()
                        );


        return ResponseEntity.ok(
                toArtifactResponse(
                        savedArtifact
                )
        );
    }


    // ----------------------------------------------------
    // BULUNTU DÜZENLE
    // ADMIN + CREW_MEMBER
    // ----------------------------------------------------

    @PutMapping("/{artifactId}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'CREW_MEMBER')"
    )
    public ResponseEntity<ArtifactResponse>
    updateArtifact(

            @PathVariable
            Long artifactId,

            @Valid
            @RequestBody
            UpdateArtifactRequest request,

            Authentication authentication
    ) {

        CustomUserPrincipal principal =
                (CustomUserPrincipal)
                        authentication.getPrincipal();


        Artifact artifact =
                fromUpdateRequest(
                        request
                );


        Artifact updatedArtifact =
                artifactService
                        .updateArtifact(
                                artifactId,
                                artifact,
                                principal.getUser()
                        );


        return ResponseEntity.ok(
                toArtifactResponse(
                        updatedArtifact
                )
        );
    }


    // ----------------------------------------------------
    // BULUNTU SİL
    // ADMIN + CREW_MEMBER
    // ----------------------------------------------------

    @DeleteMapping("/{artifactId}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'CREW_MEMBER')"
    )
    public ResponseEntity<ArtifactResponse>
    deleteArtifact(

            @PathVariable
            Long artifactId,

            Authentication authentication
    ) {

        CustomUserPrincipal principal =
                (CustomUserPrincipal)
                        authentication.getPrincipal();


        Artifact deletedArtifact =
                artifactService
                        .softDelete(
                                artifactId,
                                principal.getUser()
                        );


        return ResponseEntity.ok(
                toArtifactResponse(
                        deletedArtifact
                )
        );
    }

// ----------------------------------------------------
// BULUNTU GERİ YÜKLE
// ADMIN + CREW_MEMBER
// ----------------------------------------------------

@PostMapping("/{artifactId}/restore")
public ResponseEntity<ArtifactResponse>
restoreArtifact(

        @PathVariable
        Long artifactId,

        Authentication authentication
) {

    CustomUserPrincipal principal =
            (CustomUserPrincipal)
                    authentication.getPrincipal();

    Role role =
            principal.getUser().getRole();

    if (
            role != Role.ADMIN
                    &&
            role != Role.CREW_MEMBER
    ) {

        return ResponseEntity
                .status(403)
                .build();
    }

    Artifact restoredArtifact =
            artifactService
                    .restore(
                            artifactId,
                            principal.getUser()
                    );

    return ResponseEntity.ok(
            toArtifactResponse(
                    restoredArtifact
            )
    );
}
    // ----------------------------------------------------
    // GÖRÜNTÜLEME YETKİSİ
    // ----------------------------------------------------

    private boolean canViewArtifact(
            Artifact artifact,
            User user
    ) {

        if (
                user.getRole() == Role.ADMIN
                        ||
                user.getRole() == Role.CREW_MEMBER
        ) {

            return true;
        }


        return (
                user.getRole()
                        == Role.LOOKUP_USER

                        &&

                artifact.getVisibility()
                        == ArtifactVisibility.PUBLIC
        );
    }


    // ----------------------------------------------------
    // CREATE REQUEST -> ENTITY
    // ----------------------------------------------------

    private Artifact fromCreateRequest(
            CreateArtifactRequest request
    ) {

        Artifact artifact =
                new Artifact();


        artifact.setArtifactCode(
                request.getArtifactCode()
        );


        artifact.setType(
                request.getType()
        );


        artifact.setFormNo(
                request.getFormNo()
        );


        artifact.setInventoryNo(
                request.getInventoryNo()
        );


        artifact.setStudyNo(
                request.getStudyNo()
        );


        artifact.setBagNo(
                request.getBagNo()
        );


        artifact.setBoxNo(
                request.getBoxNo()
        );


        artifact.setDepth(
                request.getDepth()
        );


        artifact.setBox(
                request.getBox()
        );


        artifact.setFindLocation(
                request.getFindLocation()
        );


        artifact.setLocality(
                request.getLocality()
        );


        artifact.setSector(
                request.getSector()
        );


        artifact.setFindDate(
                request.getFindDate()
        );


        artifact.setFindYear(
                request.getFindYear()
        );


        artifact.setArea(
                request.getArea()
        );


        artifact.setForm(
                request.getForm()
        );


        artifact.setDecorationType(
                request.getDecorationType()
        );


        artifact.setPasteStructure(
                request.getPasteStructure()
        );


        artifact.setFiring(
                request.getFiring()
        );


        artifact.setTechnique(
                request.getTechnique()
        );


        artifact.setTemper(
                request.getTemper()
        );


        artifact.setTemperAmount(
                request.getTemperAmount()
        );


        artifact.setSlipStructure(
                request.getSlipStructure()
        );


        artifact.setAngle(
                request.getAngle()
        );


        artifact.setPeriod(
                request.getPeriod()
        );


        artifact.setKind(
                request.getKind()
        );


        artifact.setMunsell(
                request.getMunsell()
        );


        artifact.setDiameter(
                request.getDiameter()
        );


        artifact.setWeight(
                request.getWeight()
        );


        artifact.setLength(
                request.getLength()
        );


        artifact.setWidth(
                request.getWidth()
        );


        artifact.setThickness(
                request.getThickness()
        );


        artifact.setDrawingNo(
                request.getDrawingNo()
        );


        artifact.setPreservedPart(
                request.getPreservedPart()
        );


        artifact.setMaterial(
                request.getMaterial()
        );


        artifact.setProductionPlace(
                request.getProductionPlace()
        );


        artifact.setDescription(
                request.getDescription()
        );


        artifact.setBibliography(
                request.getBibliography()
        );


        artifact.setVisibility(
                request.getVisibility()
        );


        return artifact;
    }


    // ----------------------------------------------------
    // UPDATE REQUEST -> ENTITY
    // ----------------------------------------------------

    private Artifact fromUpdateRequest(
            UpdateArtifactRequest request
    ) {

        Artifact artifact =
                new Artifact();


        artifact.setArtifactCode(
                request.getArtifactCode()
        );


        artifact.setType(
                request.getType()
        );


        artifact.setFormNo(
                request.getFormNo()
        );


        artifact.setInventoryNo(
                request.getInventoryNo()
        );


        artifact.setStudyNo(
                request.getStudyNo()
        );


        artifact.setBagNo(
                request.getBagNo()
        );


        artifact.setBoxNo(
                request.getBoxNo()
        );


        artifact.setDepth(
                request.getDepth()
        );


        artifact.setBox(
                request.getBox()
        );


        artifact.setFindLocation(
                request.getFindLocation()
        );


        artifact.setLocality(
                request.getLocality()
        );


        artifact.setSector(
                request.getSector()
        );


        artifact.setFindDate(
                request.getFindDate()
        );


        artifact.setFindYear(
                request.getFindYear()
        );


        artifact.setArea(
                request.getArea()
        );


        artifact.setForm(
                request.getForm()
        );


        artifact.setDecorationType(
                request.getDecorationType()
        );


        artifact.setPasteStructure(
                request.getPasteStructure()
        );


        artifact.setFiring(
                request.getFiring()
        );


        artifact.setTechnique(
                request.getTechnique()
        );


        artifact.setTemper(
                request.getTemper()
        );


        artifact.setTemperAmount(
                request.getTemperAmount()
        );


        artifact.setSlipStructure(
                request.getSlipStructure()
        );


        artifact.setAngle(
                request.getAngle()
        );


        artifact.setPeriod(
                request.getPeriod()
        );


        artifact.setKind(
                request.getKind()
        );


        artifact.setMunsell(
                request.getMunsell()
        );


        artifact.setDiameter(
                request.getDiameter()
        );


        artifact.setWeight(
                request.getWeight()
        );


        artifact.setLength(
                request.getLength()
        );


        artifact.setWidth(
                request.getWidth()
        );


        artifact.setThickness(
                request.getThickness()
        );


        artifact.setDrawingNo(
                request.getDrawingNo()
        );


        artifact.setPreservedPart(
                request.getPreservedPart()
        );


        artifact.setMaterial(
                request.getMaterial()
        );


        artifact.setProductionPlace(
                request.getProductionPlace()
        );


        artifact.setDescription(
                request.getDescription()
        );


        artifact.setBibliography(
                request.getBibliography()
        );


        artifact.setVisibility(
                request.getVisibility()
        );


        return artifact;
    }


    // ----------------------------------------------------
    // ENTITY -> LIST RESPONSE
    // ----------------------------------------------------

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

                getUserName(
                        artifact.getUpdatedBy()
                ),

                artifact.getUpdatedAt(),

                artifact.isDeleted()
        );
    }


    // ----------------------------------------------------
    // ENTITY -> DETAIL RESPONSE
    // ----------------------------------------------------

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
                        ? artifact
                                .getCreatedBy()
                                .getId()
                        : null,

                getUserName(
                        artifact.getCreatedBy()
                ),

                artifact.getCreatedAt(),

                artifact.getUpdatedBy() != null
                        ? artifact
                                .getUpdatedBy()
                                .getId()
                        : null,

                getUserName(
                        artifact.getUpdatedBy()
                ),

                artifact.getUpdatedAt(),

                artifact.isDeleted()
        );
    }


    // ----------------------------------------------------
    // KULLANICI ADI
    // ----------------------------------------------------

    private String getUserName(
            User user
    ) {

        if (user == null) {

            return null;
        }


        return user.getFirstName()
                + " "
                + user.getLastName();
    }


    // ----------------------------------------------------
    // DOSYA ADINI GÜVENLİ HALE GETİR
    // ----------------------------------------------------

    private String sanitizeFileName(
            String value
    ) {

        if (
                value == null
                        ||
                value.isBlank()
        ) {

            return "artifact";
        }


        return value
                .trim()
                .replaceAll(
                        "[^a-zA-Z0-9._-]",
                        "_"
                );
    }
}