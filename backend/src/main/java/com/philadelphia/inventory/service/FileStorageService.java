package com.philadelphia.inventory.service;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class FileStorageService {

    private final Cloudinary cloudinary;

    public FileStorageService(
        @Value("${cloudinary.cloud-name}") String cloudName,
        @Value("${cloudinary.api-key}") String apiKey,
        @Value("${cloudinary.api-secret}") String apiSecret
) {

        this.cloudinary = new Cloudinary(
                ObjectUtils.asMap(
                        "cloud_name", cloudName,
                        "api_key", apiKey,
                        "api_secret", apiSecret,
                        "secure", true
                )
        );
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

        try {

            String publicId =
                    "philadelphia-inventory/artifacts/"
                    + artifactId
                    + "/"
                    + UUID.randomUUID();

            Map<?, ?> uploadResult =
                    cloudinary.uploader().upload(
                            file.getBytes(),
                            ObjectUtils.asMap(
                                    "public_id", publicId,
                                    "resource_type", "image"
                            )
                    );

            Object secureUrl =
                    uploadResult.get("secure_url");

            if (secureUrl == null) {
                throw new IllegalStateException(
                        "Cloudinary did not return a photo URL."
                );
            }

            return secureUrl.toString();

        } catch (IOException exception) {

            throw new IllegalStateException(
                    "Photo could not be stored in Cloudinary.",
                    exception
            );
        }
    }

    public byte[] loadFile(String storagePath) {

        if (storagePath == null
                || storagePath.isBlank()) {
            throw new IllegalArgumentException(
                    "Photo storage path is missing."
            );
        }

        try {

            java.net.URI uri =
                    java.net.URI.create(storagePath);

            return uri.toURL()
                    .openStream()
                    .readAllBytes();

        } catch (Exception exception) {

            throw new IllegalArgumentException(
                    "Photo file could not be loaded.",
                    exception
            );
        }
    }
}