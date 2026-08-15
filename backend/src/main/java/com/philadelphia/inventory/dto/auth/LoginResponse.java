package com.philadelphia.inventory.dto.auth;

import com.philadelphia.inventory.entity.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private Long userId;

    private String firstName;

    private String lastName;

    private String email;

    private Role role;

    private boolean mustChangePassword;
}