package com.philadelphia.inventory.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.philadelphia.inventory.dto.artifact.ArtifactPhotoResponse;
import com.philadelphia.inventory.entity.Artifact;
import com.philadelphia.inventory.entity.ArtifactPhoto;
import com.philadelphia.inventory.entity.User;
import com.philadelphia.inventory.entity.enums.ArtifactVisibility;
import com.philadelphia.inventory.entity.enums.Role;
import com.philadelphia.inventory.security.CustomUserPrincipal;
import com.philadelphia.inventory.service.ArtifactPhotoService;
import com.philadelphia.inventory.service.ArtifactService;

@RestController
@RequestMapping("/api/artifacts")
public class ArtifactPhotoController {

    private final ArtifactPhotoService artifactPhotoService;
    private final ArtifactService artifactService;

    public ArtifactPhotoController(
            ArtifactPhotoService artifactPhotoService,
            ArtifactService artifactService
    ) {
        this.artifactPhotoService = artifactPhotoService;
        this.artifactService = artifactService;
    }

    // Aktif fotoğrafları getir.
    // LOOKUP_USER yalnızca PUBLIC buluntu fotoğraflarını görebilir.
    @GetMapping("/{artifactId}/photos")
    public ResponseEntity<List<ArtifactPhotoResponse>> getArtifactPhotos(
            @PathVariable Long artifactId,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {

        Artifact artifact =
                artifactService.getArtifactEntityById(artifactId);

        checkViewPermission(
                artifact,
                principal.getUser()
        );

        List<ArtifactPhotoResponse> response =
                artifactPhotoService
                        .getActivePhotos(artifactId)
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    // Silinmiş fotoğrafları yalnızca ADMIN görebilir.
    @GetMapping("/{artifactId}/photos/deleted")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ArtifactPhotoResponse>>
    getDeletedArtifactPhotos(
            @PathVariable Long artifactId
    ) {

        artifactService.getArtifactEntityById(artifactId);

        List<ArtifactPhotoResponse> response =
                artifactPhotoService
                        .getDeletedPhotos(artifactId)
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    // Fotoğraf yükleme:
    // ADMIN ve CREW_MEMBER kullanabilir.
    @PostMapping(
            value = "/{artifactId}/photos",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'CREW_MEMBER')"
    )
    public ResponseEntity<ArtifactPhotoResponse> uploadPhoto(
            @PathVariable Long artifactId,
            @RequestParam(required = false) String photoNo,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {

        Artifact artifact =
                artifactService.getArtifactEntityById(artifactId);

        User user = principal.getUser();

        ArtifactPhoto photo =
                artifactPhotoService.uploadPhoto(
                        artifact,
                        photoNo,
                        file,
                        user
                );

        return ResponseEntity.ok(
                toResponse(photo)
        );
    }

    // Fotoğraf dosyasını görüntüle.
    // LOOKUP_USER yalnızca PUBLIC buluntu fotoğrafını açabilir.
    @GetMapping("/photos/{photoId}/file")
    public ResponseEntity<byte[]> getPhotoFile(
            @PathVariable Long photoId,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {

        ArtifactPhoto photo =
                artifactPhotoService.getPhoto(photoId);

        if (photo.isDeleted()) {
            throw new IllegalArgumentException(
                    "Photo is deleted."
            );
        }

        Long artifactId =
                photo.getArtifact().getId();

        Artifact artifact =
                artifactService.getArtifactEntityById(
                        artifactId
                );

        checkViewPermission(
                artifact,
                principal.getUser()
        );

        byte[] file =
                artifactPhotoService.loadPhotoFile(
                        photoId
                );

        MediaType mediaType =
                photo.getContentType() != null
                        ? MediaType.parseMediaType(
                                photo.getContentType()
                        )
                        : MediaType.APPLICATION_OCTET_STREAM;

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" +
                                photo.getFileName() +
                                "\""
                )
                .body(file);
    }

    // Fotoğraf soft delete:
    // Güvenli tarafta kalmak için yalnızca ADMIN.
    @DeleteMapping("/photos/{photoId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ArtifactPhotoResponse> deletePhoto(
            @PathVariable Long photoId,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {

        User user = principal.getUser();

        ArtifactPhoto photo =
                artifactPhotoService.softDeletePhoto(
                        photoId,
                        user
                );

        return ResponseEntity.ok(
                toResponse(photo)
        );
    }

    // Silinen fotoğrafı geri getir:
    // yalnızca ADMIN.
    @PostMapping("/photos/{photoId}/restore")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ArtifactPhotoResponse> restorePhoto(
            @PathVariable Long photoId
    ) {

        ArtifactPhoto photo =
                artifactPhotoService.restorePhoto(
                        photoId
                );

        return ResponseEntity.ok(
                toResponse(photo)
        );
    }

    // PUBLIC / PRIVATE_FOR_CREW kontrolü.
    private void checkViewPermission(
            Artifact artifact,
            User user
    ) {

        if (user.getRole() == Role.ADMIN
                || user.getRole() == Role.CREW_MEMBER) {
            return;
        }

        if (user.getRole() == Role.LOOKUP_USER
                && artifact.getVisibility()
                == ArtifactVisibility.PUBLIC) {
            return;
        }

        throw new org.springframework.security
                .access.AccessDeniedException(
                        "You do not have permission to view this artifact."
                );
    }

    // Entity -> DTO dönüşümü.
    private ArtifactPhotoResponse toResponse(
            ArtifactPhoto photo
    ) {

        User uploadedBy =
                photo.getUploadedBy();

        User deletedBy =
                photo.getDeletedBy();

        return new ArtifactPhotoResponse(
                photo.getId(),

                photo.getArtifact() != null
                        ? photo.getArtifact().getId()
                        : null,

                photo.getPhotoNo(),
                photo.getFileName(),
                photo.getContentType(),

                uploadedBy != null
                        ? uploadedBy.getId()
                        : null,

                getUserName(uploadedBy),

                photo.getUploadedAt(),
                photo.isDeleted(),

                deletedBy != null
                        ? deletedBy.getId()
                        : null,

                getUserName(deletedBy),

                photo.getDeletedAt()
        );
    }

    private String getUserName(User user) {

        if (user == null) {
            return null;
        }

        return user.getFirstName()
                + " "
                + user.getLastName();
    }
}