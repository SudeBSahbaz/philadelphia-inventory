package com.philadelphia.inventory.dto.user;

import java.time.LocalDateTime;

import com.philadelphia.inventory.entity.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private Role role;

    private boolean active;

    private boolean mustChangePassword;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}