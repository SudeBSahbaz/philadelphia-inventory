package com.philadelphia.inventory.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.philadelphia.inventory.entity.Artifact;
import com.philadelphia.inventory.entity.User;
import com.philadelphia.inventory.entity.enums.ArtifactVisibility;
import com.philadelphia.inventory.repository.ArtifactRepository;

import jakarta.persistence.criteria.Predicate;

@Service
public class ArtifactService {

    private final ArtifactRepository artifactRepository;
    private final ArtifactChangeLogService changeLogService;

    public ArtifactService(
            ArtifactRepository artifactRepository,
            ArtifactChangeLogService changeLogService
    ) {
        this.artifactRepository = artifactRepository;
        this.changeLogService = changeLogService;
    }


    // --------------------------------------------------
    // AKTİF BULUNTULAR
    // --------------------------------------------------

    public List<Artifact> getAllActiveArtifacts() {

        return artifactRepository
                .findAllByDeletedFalse();
    }


    // --------------------------------------------------
    // SİLİNMİŞ BULUNTULAR
    // --------------------------------------------------

    public List<Artifact> getAllDeletedArtifacts() {

        return artifactRepository
                .findAllByDeletedTrue();
    }


    // --------------------------------------------------
    // PUBLIC BULUNTULAR
    // --------------------------------------------------

    public List<Artifact> getPublicArtifacts() {

        return artifactRepository
                .findAllByVisibilityAndDeletedFalse(
                        ArtifactVisibility.PUBLIC
                );
    }


    // --------------------------------------------------
    // BULUNTU KODU İLE GETİR
    // --------------------------------------------------

    public Optional<Artifact> getByArtifactCode(
            String artifactCode
    ) {

        return artifactRepository
                .findByArtifactCodeAndDeletedFalse(
                        artifactCode
                );
    }


    // --------------------------------------------------
    // GELİŞMİŞ ARAMA
    // --------------------------------------------------

    public List<Artifact> searchArtifacts(
            String artifactCode,
            String type,
            String findLocation,
            String locality,
            String sector,
            Integer findYear,
            String period,
            String material,
            String form,
            String decorationType,
            String technique,
            String munsell,
            ArtifactVisibility visibility
    ) {

        Specification<Artifact> specification =
                (root, query, criteriaBuilder) -> {

                    List<Predicate> predicates =
                            new ArrayList<>();


                    // Silinmiş kayıtlar aramaya dahil edilmez.

                    predicates.add(
                            criteriaBuilder.isFalse(
                                    root.get("deleted")
                            )
                    );


                    if (hasText(artifactCode)) {

                        predicates.add(
                                criteriaBuilder.like(
                                        criteriaBuilder.lower(
                                                root.get(
                                                        "artifactCode"
                                                )
                                        ),
                                        contains(
                                                artifactCode
                                        )
                                )
                        );
                    }


                    if (hasText(type)) {

                        predicates.add(
                                criteriaBuilder.like(
                                        criteriaBuilder.lower(
                                                root.get(
                                                        "type"
                                                )
                                        ),
                                        contains(
                                                type
                                        )
                                )
                        );
                    }


                    if (hasText(findLocation)) {

                        predicates.add(
                                criteriaBuilder.like(
                                        criteriaBuilder.lower(
                                                root.get(
                                                        "findLocation"
                                                )
                                        ),
                                        contains(
                                                findLocation
                                        )
                                )
                        );
                    }


                    if (hasText(locality)) {

                        predicates.add(
                                criteriaBuilder.like(
                                        criteriaBuilder.lower(
                                                root.get(
                                                        "locality"
                                                )
                                        ),
                                        contains(
                                                locality
                                        )
                                )
                        );
                    }


                    if (hasText(sector)) {

                        predicates.add(
                                criteriaBuilder.like(
                                        criteriaBuilder.lower(
                                                root.get(
                                                        "sector"
                                                )
                                        ),
                                        contains(
                                                sector
                                        )
                                )
                        );
                    }


                    if (findYear != null) {

                        predicates.add(
                                criteriaBuilder.equal(
                                        root.get(
                                                "findYear"
                                        ),
                                        findYear
                                )
                        );
                    }


                    if (hasText(period)) {

                        predicates.add(
                                criteriaBuilder.like(
                                        criteriaBuilder.lower(
                                                root.get(
                                                        "period"
                                                )
                                        ),
                                        contains(
                                                period
                                        )
                                )
                        );
                    }


                    if (hasText(material)) {

                        predicates.add(
                                criteriaBuilder.like(
                                        criteriaBuilder.lower(
                                                root.get(
                                                        "material"
                                                )
                                        ),
                                        contains(
                                                material
                                        )
                                )
                        );
                    }


                    if (hasText(form)) {

                        predicates.add(
                                criteriaBuilder.like(
                                        criteriaBuilder.lower(
                                                root.get(
                                                        "form"
                                                )
                                        ),
                                        contains(
                                                form
                                        )
                                )
                        );
                    }


                    if (hasText(decorationType)) {

                        predicates.add(
                                criteriaBuilder.like(
                                        criteriaBuilder.lower(
                                                root.get(
                                                        "decorationType"
                                                )
                                        ),
                                        contains(
                                                decorationType
                                        )
                                )
                        );
                    }


                    if (hasText(technique)) {

                        predicates.add(
                                criteriaBuilder.like(
                                        criteriaBuilder.lower(
                                                root.get(
                                                        "technique"
                                                )
                                        ),
                                        contains(
                                                technique
                                        )
                                )
                        );
                    }


                    if (hasText(munsell)) {

                        predicates.add(
                                criteriaBuilder.like(
                                        criteriaBuilder.lower(
                                                root.get(
                                                        "munsell"
                                                )
                                        ),
                                        contains(
                                                munsell
                                        )
                                )
                        );
                    }


                    if (visibility != null) {

                        predicates.add(
                                criteriaBuilder.equal(
                                        root.get(
                                                "visibility"
                                        ),
                                        visibility
                                )
                        );
                    }


                    return criteriaBuilder.and(
                            predicates.toArray(
                                    new Predicate[0]
                            )
                    );
                };


        return artifactRepository.findAll(
                specification
        );
    }


    private boolean hasText(
            String value
    ) {

        return value != null
                && !value.isBlank();
    }


    private String contains(
            String value
    ) {

        return "%"
                + value.trim()
                .toLowerCase()
                + "%";
    }

// --------------------------------------------------
// BULUNTU KODU DURUMU
// ACTIVE / DELETED / AVAILABLE
// --------------------------------------------------

public String getArtifactCodeStatus(
        String artifactCode
) {

    if (
            artifactCode == null ||
            artifactCode.isBlank()
    ) {

        return "AVAILABLE";
    }

    return artifactRepository
            .findByArtifactCode(
                    artifactCode.trim()
            )
            .map(
                    artifact ->
                            artifact.isDeleted()
                                    ? "DELETED"
                                    : "ACTIVE"
            )
            .orElse(
                    "AVAILABLE"
            );
}
    // --------------------------------------------------
    // ID İLE AKTİF BULUNTU GETİR
    // --------------------------------------------------

    public Artifact getArtifactEntityById(
            Long id
    ) {

        Artifact artifact =
                artifactRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Buluntu bulunamadı."
                                )
                        );


        if (artifact.isDeleted()) {

            throw new IllegalArgumentException(
                    "Silinmiş bir buluntu bu işlem için kullanılamaz."
            );
        }


        return artifact;
    }


    // --------------------------------------------------
    // BULUNTU OLUŞTUR
    // --------------------------------------------------

    public Artifact createArtifact(
            Artifact artifact,
            User createdBy
    ) {

        if (
                artifact.getArtifactCode() == null
                        ||
                artifact.getArtifactCode()
                        .isBlank()
        ) {

            throw new IllegalArgumentException(
                    "Buluntu kodu zorunludur."
            );
        }


        if (
                artifactRepository
                        .existsByArtifactCode(
                                artifact.getArtifactCode()
                        )
        ) {

            throw new IllegalArgumentException(
                    "Bu kodla kayıtlı bir buluntu zaten bulunmaktadır."
            );
        }


        if (
                artifact.getVisibility() == null
        ) {

            throw new IllegalArgumentException(
                    "Buluntu görünürlüğü seçilmelidir."
            );
        }


        artifact.setCreatedBy(
                createdBy
        );

        artifact.setUpdatedBy(
                createdBy
        );


        LocalDateTime now =
                LocalDateTime.now();


        artifact.setCreatedAt(
                now
        );

        artifact.setUpdatedAt(
                now
        );

        artifact.setDeleted(
                false
        );


        Artifact savedArtifact =
                artifactRepository.save(
                        artifact
                );


        changeLogService.recordCreated(
                savedArtifact,
                createdBy
        );


        return savedArtifact;
    }


    // --------------------------------------------------
    // BULUNTU GÜNCELLE
    // --------------------------------------------------

    public Artifact updateArtifact(
            Long artifactId,
            Artifact updatedArtifact,
            User updatedBy
    ) {

        Artifact existingArtifact =
                artifactRepository
                        .findById(
                                artifactId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Buluntu bulunamadı."
                                )
                        );


        if (
                existingArtifact.isDeleted()
        ) {

            throw new IllegalArgumentException(
                    "Silinmiş buluntular düzenlenemez."
            );
        }


        String newArtifactCode =
                updatedArtifact
                        .getArtifactCode();


        if (
                newArtifactCode == null
                        ||
                newArtifactCode.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "Buluntu kodu zorunludur."
            );
        }


        if (
                !existingArtifact
                        .getArtifactCode()
                        .equals(
                                newArtifactCode
                        )
                        &&
                artifactRepository
                        .existsByArtifactCode(
                                newArtifactCode
                        )
        ) {

            throw new IllegalArgumentException(
                    "Bu kodla kayıtlı bir buluntu zaten bulunmaktadır."
            );
        }


        changeLogService.recordUpdated(
                existingArtifact,
                updatedArtifact,
                updatedBy
        );


        updatedArtifact.setId(
                existingArtifact.getId()
        );


        updatedArtifact.setCreatedBy(
                existingArtifact.getCreatedBy()
        );


        updatedArtifact.setCreatedAt(
                existingArtifact.getCreatedAt()
        );


        updatedArtifact.setUpdatedBy(
                updatedBy
        );


        updatedArtifact.setUpdatedAt(
                LocalDateTime.now()
        );


        updatedArtifact.setDeleted(
                false
        );

        updatedArtifact.setDeletedBy(
                null
        );

        updatedArtifact.setDeletedAt(
                null
        );


        return artifactRepository.save(
                updatedArtifact
        );
    }


    // --------------------------------------------------
    // SOFT DELETE
    // --------------------------------------------------

    public Artifact softDelete(
            Long artifactId,
            User deletedBy
    ) {

        Artifact artifact =
                artifactRepository
                        .findById(
                                artifactId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Buluntu bulunamadı."
                                )
                        );


        if (
                artifact.isDeleted()
        ) {

            throw new IllegalArgumentException(
                    "Bu buluntu zaten silinmiş."
            );
        }


        artifact.setDeleted(
                true
        );

        artifact.setDeletedBy(
                deletedBy
        );

        artifact.setDeletedAt(
                LocalDateTime.now()
        );


        artifact.setUpdatedBy(
                deletedBy
        );

        artifact.setUpdatedAt(
                LocalDateTime.now()
        );


        Artifact savedArtifact =
                artifactRepository.save(
                        artifact
                );


        changeLogService.recordDeleted(
                savedArtifact,
                deletedBy
        );


        return savedArtifact;
    }


    // --------------------------------------------------
    // GERİ YÜKLE
    // --------------------------------------------------

    public Artifact restore(
            Long artifactId,
            User restoredBy
    ) {

        Artifact artifact =
                artifactRepository
                        .findById(
                                artifactId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Buluntu bulunamadı."
                                )
                        );


        if (
                !artifact.isDeleted()
        ) {

            throw new IllegalArgumentException(
                    "Bu buluntu silinmiş durumda değil."
            );
        }


        artifact.setDeleted(
                false
        );

        artifact.setDeletedBy(
                null
        );

        artifact.setDeletedAt(
                null
        );


        artifact.setUpdatedBy(
                restoredBy
        );

        artifact.setUpdatedAt(
                LocalDateTime.now()
        );


        Artifact savedArtifact =
                artifactRepository.save(
                        artifact
                );


        changeLogService.recordRestored(
                savedArtifact,
                restoredBy
        );


        return savedArtifact;
    }
}