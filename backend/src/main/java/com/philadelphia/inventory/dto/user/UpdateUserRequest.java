package com.philadelphia.inventory.dto.user;

import com.philadelphia.inventory.entity.enums.Role;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {

    private String firstName;

    private String lastName;

    private String email;

    private Role role;

    private Boolean active;
}