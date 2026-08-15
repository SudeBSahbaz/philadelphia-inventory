package com.philadelphia.inventory.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {

    @NotBlank(message = "Mevcut şifre boş bırakılamaz.")
    private String currentPassword;

    @NotBlank(message = "Yeni şifre boş bırakılamaz.")
    @Size(
            min = 8,
            max = 100,
            message = "Yeni şifre en az 8 karakter olmalıdır."
    )
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
            message = "Yeni şifre en az bir harf ve bir rakam içermelidir."
    )
    private String newPassword;

    @NotBlank(message = "Yeni şifre tekrarı boş bırakılamaz.")
    private String newPasswordConfirm;
}