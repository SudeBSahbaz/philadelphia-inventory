package com.philadelphia.inventory.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.philadelphia.inventory.dto.user.CreateUserRequest;
import com.philadelphia.inventory.dto.user.UpdateUserRequest;
import com.philadelphia.inventory.dto.user.UserResponse;
import com.philadelphia.inventory.entity.User;
import com.philadelphia.inventory.security.CustomUserPrincipal;
import com.philadelphia.inventory.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(
            UserService userService
    ) {
        this.userService =
                userService;
    }


    // ----------------------------------------------------
    // TÜM KULLANICILARI GETİR
    // SADECE ADMIN
    // ----------------------------------------------------

    @GetMapping
    @PreAuthorize(
            "hasRole('ADMIN')"
    )
    public ResponseEntity<List<UserResponse>>
    getAllUsers() {

        List<UserResponse> response =
                userService
                        .getAllUsers()
                        .stream()
                        .map(
                                this::toResponse
                        )
                        .toList();

        return ResponseEntity.ok(
                response
        );
    }


    // ----------------------------------------------------
    // ID İLE KULLANICI GETİR
    // SADECE ADMIN
    // ----------------------------------------------------

    @GetMapping("/{id}")
    @PreAuthorize(
            "hasRole('ADMIN')"
    )
    public ResponseEntity<UserResponse>
    getUserById(
            @PathVariable
            Long id
    ) {

        return userService
                .getUserById(id)
                .map(
                        this::toResponse
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
    // YENİ KULLANICI OLUŞTUR
    // SADECE ADMIN
    // ----------------------------------------------------

    @PostMapping
    @PreAuthorize(
            "hasRole('ADMIN')"
    )
    public ResponseEntity<UserResponse>
    createUser(
            @Valid
            @RequestBody
            CreateUserRequest request
    ) {

        User user =
                new User();

        user.setFirstName(
                request.getFirstName()
        );

        user.setLastName(
                request.getLastName()
        );

        user.setEmail(
                request.getEmail()
        );

        user.setRole(
                request.getRole()
        );

        User savedUser =
                userService.createUser(
                        user,
                        request.getPassword()
                );

        return ResponseEntity.ok(
                toResponse(
                        savedUser
                )
        );
    }


    // ----------------------------------------------------
    // KULLANICI GÜNCELLE
    // SADECE ADMIN
    // ----------------------------------------------------

    @PutMapping("/{id}")
    @PreAuthorize(
            "hasRole('ADMIN')"
    )
    public ResponseEntity<UserResponse>
    updateUser(
            @PathVariable
            Long id,

            @Valid
            @RequestBody
            UpdateUserRequest request,

            Authentication authentication
    ) {

        CustomUserPrincipal principal =
                (CustomUserPrincipal)
                        authentication.getPrincipal();


        // --------------------------------------------------
        // KENDİ ROLÜNÜ DEĞİŞTİREMEZ
        // --------------------------------------------------

        if (
                principal.getId()
                        .equals(id)
        ) {

            User existingUser =
                    userService
                            .getUserById(id)
                            .orElseThrow(
                                    () ->
                                            new IllegalArgumentException(
                                                    "User not found."
                                            )
                            );

            if (
                    existingUser.getRole()
                            != request.getRole()
            ) {

                throw new IllegalArgumentException(
                        "You cannot change your own role."
                );
            }
        }


        User user =
                new User();

        user.setFirstName(
                request.getFirstName()
        );

        user.setLastName(
                request.getLastName()
        );

        user.setEmail(
                request.getEmail()
        );

        user.setRole(
                request.getRole()
        );


        User updatedUser =
                userService.updateUser(
                        id,
                        user
                );


        return ResponseEntity.ok(
                toResponse(
                        updatedUser
                )
        );
    }


    // ----------------------------------------------------
    // KULLANICIYI PASİF YAP
    // SADECE ADMIN
    // ----------------------------------------------------

    @PostMapping("/{id}/deactivate")
    @PreAuthorize(
            "hasRole('ADMIN')"
    )
    public ResponseEntity<UserResponse>
    deactivateUser(
            @PathVariable
            Long id,

            Authentication authentication
    ) {

        CustomUserPrincipal principal =
                (CustomUserPrincipal)
                        authentication.getPrincipal();


        // --------------------------------------------------
        // KENDİ HESABINI PASİF YAPAMAZ
        // --------------------------------------------------

        if (
                principal.getId()
                        .equals(id)
        ) {

            throw new IllegalArgumentException(
                    "You cannot deactivate your own account."
            );
        }


        User user =
                userService
                        .deactivateUser(id);


        return ResponseEntity.ok(
                toResponse(
                        user
                )
        );
    }


    // ----------------------------------------------------
    // KULLANICIYI TEKRAR AKTİF YAP
    // SADECE ADMIN
    // ----------------------------------------------------

    @PostMapping("/{id}/activate")
    @PreAuthorize(
            "hasRole('ADMIN')"
    )
    public ResponseEntity<UserResponse>
    activateUser(
            @PathVariable
            Long id
    ) {

        User user =
                userService
                        .activateUser(id);

        return ResponseEntity.ok(
                toResponse(
                        user
                )
        );
    }


    // ----------------------------------------------------
    // ENTITY -> RESPONSE
    // ----------------------------------------------------

    private UserResponse toResponse(
            User user
    ) {

        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                user.isActive(),
                user.isMustChangePassword(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}