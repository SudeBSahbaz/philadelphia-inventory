package com.philadelphia.inventory.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private final Path uploadRoot;

    public FileStorageService(
            @Value("${app.upload.dir}") String uploadDir
    ) {
        this.uploadRoot = Paths.get(uploadDir)
                .toAbsolutePath()
                .normalize();
    }

    public String storeArtifactPhoto(
            Long artifactId,
            MultipartFile file
    ) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Photo file is required."
            );
        }

        String contentType = file.getContentType();

        if (contentType == null
                || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException(
                    "Only image files are allowed."
            );
        }

        String originalFileName = file.getOriginalFilename();

        String extension = "";

        if (originalFileName != null
                && originalFileName.contains(".")) {

            extension = originalFileName.substring(
                    originalFileName.lastIndexOf(".")
            );
        }

        String storedFileName =
                UUID.randomUUID() + extension;

        Path artifactDirectory =
                uploadRoot.resolve(
                        artifactId.toString()
                );

        try {
            Files.createDirectories(artifactDirectory);

            Path targetPath =
                    artifactDirectory.resolve(
                            storedFileName
                    );

            Files.copy(
                    file.getInputStream(),
                    targetPath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            return targetPath.toString();

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Photo could not be stored.",
                    exception
            );
        }
    }

    public byte[] loadFile(String storagePath) {

        try {
            return Files.readAllBytes(
                    Paths.get(storagePath)
            );

        } catch (IOException exception) {
            throw new IllegalArgumentException(
                    "Photo file could not be found."
            );
        }
    }
}