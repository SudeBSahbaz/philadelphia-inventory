package com.philadelphia.inventory.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.philadelphia.inventory.dto.user.UserResponse;
import com.philadelphia.inventory.entity.User;
import com.philadelphia.inventory.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Tüm kullanıcıları listele
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        List<UserResponse> response =
                userService.getAllUsers()
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    // ID ile kullanıcı getir
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id
    ) {
        return userService.getUserById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private UserResponse toResponse(User user) {
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