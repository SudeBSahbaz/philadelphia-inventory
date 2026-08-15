package com.philadelphia.inventory.dto.auth;

import com.philadelphia.inventory.entity.enums.Role;

public record ProfileResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        Role role,
        boolean mustChangePassword
) {
}