package com.philadelphia.inventory.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class FileStorageService {


    private final String cloudName;
    private final String apiKey;
    private final String apiSecret;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;


    public FileStorageService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret
    ) {

        this.cloudName = cloudName;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;

        this.httpClient =
                HttpClient.newHttpClient();

        this.objectMapper =
                new ObjectMapper();
    }


    // ==================================================
    // STORE ARTIFACT PHOTO
    // ==================================================

    public String storeArtifactPhoto(
            Long artifactId,
            MultipartFile file
    ) {

        if (
                file == null ||
                file.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Photo file is required."
            );
        }


        String contentType =
                file.getContentType();


        if (
                contentType == null ||
                !contentType.startsWith("image/")
        ) {

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


            String boundary =
                    "----PhiladelphiaInventory"
                    + UUID.randomUUID();


            byte[] requestBody =
                    createMultipartBody(
                            boundary,
                            publicId,
                            file
                    );


            String credentials =
                    apiKey
                    + ":"
                    + apiSecret;


            String basicAuthentication =
                    Base64.getEncoder()
                            .encodeToString(
                                    credentials.getBytes(
                                            StandardCharsets.UTF_8
                                    )
                            );


            URI uploadUri =
                    URI.create(
                            "https://api.cloudinary.com/v1_1/"
                            + cloudName
                            + "/image/upload"
                    );


            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(uploadUri)

                            .header(
                                    "Authorization",
                                    "Basic "
                                    + basicAuthentication
                            )

                            .header(
                                    "Content-Type",
                                    "multipart/form-data; boundary="
                                    + boundary
                            )

                            // Brotli response negotiation yapma.
                            .header(
                                    "Accept-Encoding",
                                    "identity"
                            )

                            .POST(
                                    HttpRequest.BodyPublishers
                                            .ofByteArray(
                                                requestBody
                                            )
                            )

                            .build();


            HttpResponse<String> response =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );


            if (
                    response.statusCode() < 200 ||
                    response.statusCode() >= 300
            ) {

                throw new IllegalStateException(
                        "Cloudinary upload failed. HTTP "
                        + response.statusCode()
                        + ": "
                        + response.body()
                );
            }


            JsonNode responseJson =
                    objectMapper.readTree(
                            response.body()
                    );


            JsonNode secureUrl =
                    responseJson.get(
                            "secure_url"
                    );


            if (
                    secureUrl == null ||
                    secureUrl.asText().isBlank()
            ) {

                throw new IllegalStateException(
                        "Cloudinary did not return a photo URL."
                );
            }


            return secureUrl.asText();


        } catch (
                IOException |
                InterruptedException exception
        ) {

            if (
                    exception
                            instanceof InterruptedException
            ) {

                Thread.currentThread()
                        .interrupt();
            }


            throw new IllegalStateException(
                    "Photo could not be stored in Cloudinary.",
                    exception
            );
        }
    }


    // ==================================================
    // CREATE MULTIPART BODY
    // ==================================================

    private byte[] createMultipartBody(
            String boundary,
            String publicId,
            MultipartFile file
    ) throws IOException {

        ByteArrayOutputStream output =
                new ByteArrayOutputStream();


        writeTextPart(
                output,
                boundary,
                "public_id",
                publicId
        );


        String originalFilename =
                file.getOriginalFilename();


        if (
                originalFilename == null ||
                originalFilename.isBlank()
        ) {

            originalFilename =
                    "artifact-photo";
        }


        output.write(
                (
                    "--"
                    + boundary
                    + "\r\n"
                ).getBytes(
                        StandardCharsets.UTF_8
                )
        );


        output.write(
                (
                    "Content-Disposition: form-data; "
                    + "name=\"file\"; filename=\""
                    + sanitizeFilename(
                            originalFilename
                    )
                    + "\"\r\n"
                ).getBytes(
                        StandardCharsets.UTF_8
                )
        );


        output.write(
                (
                    "Content-Type: "
                    + file.getContentType()
                    + "\r\n\r\n"
                ).getBytes(
                        StandardCharsets.UTF_8
                )
        );


        output.write(
                file.getBytes()
        );


        output.write(
                "\r\n".getBytes(
                        StandardCharsets.UTF_8
                )
        );


        output.write(
                (
                    "--"
                    + boundary
                    + "--\r\n"
                ).getBytes(
                        StandardCharsets.UTF_8
                )
        );


        return output.toByteArray();
    }


    // ==================================================
    // WRITE TEXT MULTIPART FIELD
    // ==================================================

    private void writeTextPart(
            ByteArrayOutputStream output,
            String boundary,
            String fieldName,
            String value
    ) throws IOException {

        output.write(
                (
                    "--"
                    + boundary
                    + "\r\n"
                ).getBytes(
                        StandardCharsets.UTF_8
                )
        );


        output.write(
                (
                    "Content-Disposition: form-data; "
                    + "name=\""
                    + fieldName
                    + "\"\r\n\r\n"
                ).getBytes(
                        StandardCharsets.UTF_8
                )
        );


        output.write(
                value.getBytes(
                        StandardCharsets.UTF_8
                )
        );


        output.write(
                "\r\n".getBytes(
                        StandardCharsets.UTF_8
                )
        );
    }


    // ==================================================
    // SANITIZE FILENAME
    // ==================================================

    private String sanitizeFilename(
            String filename
    ) {

        return filename
                .replace(
                        "\"",
                        ""
                )
                .replace(
                        "\r",
                        ""
                )
                .replace(
                        "\n",
                        ""
                );
    }


    // ==================================================
    // LOAD FILE
    // ==================================================

    public byte[] loadFile(
            String storagePath
    ) {

        if (
                storagePath == null ||
                storagePath.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "Photo storage path is missing."
            );
        }


        try {

            URI uri =
                    URI.create(
                            storagePath
                    );


            return uri
                    .toURL()
                    .openStream()
                    .readAllBytes();


        } catch (
                Exception exception
        ) {

            throw new IllegalArgumentException(
                    "Photo file could not be loaded.",
                    exception
            );
        }
    }
}