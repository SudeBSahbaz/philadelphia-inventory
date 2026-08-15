package com.philadelphia.inventory.auth;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.philadelphia.inventory.dto.auth.ChangePasswordRequest;
import com.philadelphia.inventory.dto.auth.ForgotPasswordRequest;
import com.philadelphia.inventory.dto.auth.LoginRequest;
import com.philadelphia.inventory.dto.auth.LoginResponse;
import com.philadelphia.inventory.dto.auth.ProfileResponse;
import com.philadelphia.inventory.dto.auth.ProfileUpdateRequest;
import com.philadelphia.inventory.dto.auth.ResetPasswordRequest;
import com.philadelphia.inventory.entity.User;
import com.philadelphia.inventory.repository.UserRepository;
import com.philadelphia.inventory.security.CustomUserPrincipal;
import com.philadelphia.inventory.service.PasswordResetService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final PasswordResetService passwordResetService;


    public AuthController(
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            PasswordResetService passwordResetService
    ) {

        this.authenticationManager =
                authenticationManager;

        this.passwordEncoder =
                passwordEncoder;

        this.userRepository =
                userRepository;

        this.passwordResetService =
                passwordResetService;
    }


    // ==================================================
    // GİRİŞ
    // ==================================================

    @PostMapping("/login")
public ResponseEntity<LoginResponse> login(
        @RequestBody
        LoginRequest request,

        HttpServletRequest httpRequest
) {

    Authentication authentication =
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );


    SecurityContext securityContext =
            SecurityContextHolder
                    .createEmptyContext();


    securityContext.setAuthentication(
            authentication
    );


    SecurityContextHolder.setContext(
            securityContext
    );


    HttpSession session =
            httpRequest.getSession(
                    true
            );


    // --------------------------------------------------
    // BENİ HATIRLA / SESSION SÜRESİ
    // --------------------------------------------------

    if (request.isRememberMe()) {

        // 30 gün
        session.setMaxInactiveInterval(
                60 * 60 * 24 * 30
        );

    } else {

        // 30 dakika
        session.setMaxInactiveInterval(
                60 * 30
        );
    }


    session.setAttribute(
            HttpSessionSecurityContextRepository
                    .SPRING_SECURITY_CONTEXT_KEY,
            securityContext
    );


    CustomUserPrincipal principal =
            (CustomUserPrincipal)
                    authentication
                            .getPrincipal();


    User user =
            principal.getUser();


    return ResponseEntity.ok(
            new LoginResponse(
                    principal.getId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getRole(),
                    user.isMustChangePassword()
            )
    );
}
    // ==================================================
    // ŞİFREMİ UNUTTUM
    // ==================================================

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>>
    forgotPassword(
            @Valid
            @RequestBody
            ForgotPasswordRequest request
    ) {

        passwordResetService
                .requestPasswordReset(
                        request.getEmail()
                );


        /*
         * Kullanıcı sistemde kayıtlı değilse
         * veya pasif durumdaysa mail gönderilmez.
         *
         * Fakat dışarıya bunun bilgisi verilmez.
         *
         * Kullanıcı her durumda aynı mesajı görür.
         */

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Şifre sıfırlama bağlantısı e-posta adresinize gönderildi."
                )
        );
    }


    // ==================================================
    // ŞİFRE SIFIRLAMA
    // ==================================================

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>>
    resetPassword(
            @Valid
            @RequestBody
            ResetPasswordRequest request
    ) {

        passwordResetService
                .resetPassword(
                        request.getToken(),
                        request.getNewPassword(),
                        request.getNewPasswordConfirm()
                );


        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Şifreniz başarıyla değiştirildi."
                )
        );
    }


    // ==================================================
    // KENDİ PROFİLİMİ GETİR
    // ==================================================

    @GetMapping("/me")
    public ResponseEntity<ProfileResponse>
    getMyProfile(
            Authentication authentication
    ) {

        CustomUserPrincipal principal =
                (CustomUserPrincipal)
                        authentication
                                .getPrincipal();


        User user =
                userRepository
                        .findById(
                                principal.getId()
                        )
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "User not found."
                                        )
                        );


        return ResponseEntity.ok(
                toProfileResponse(
                        user
                )
        );
    }


    // ==================================================
    // KENDİ PROFİLİMİ GÜNCELLE
    // ==================================================

    @PutMapping("/me")
    public ResponseEntity<ProfileResponse>
    updateMyProfile(
            @Valid
            @RequestBody
            ProfileUpdateRequest request,

            Authentication authentication
    ) {

        CustomUserPrincipal principal =
                (CustomUserPrincipal)
                        authentication
                                .getPrincipal();


        User user =
                userRepository
                        .findById(
                                principal.getId()
                        )
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "User not found."
                                        )
                        );


        String email =
                request
                        .getEmail()
                        .trim();


        userRepository
                .findByEmail(
                        email
                )
                .ifPresent(
                        existingUser -> {

                            if (
                                    !existingUser
                                            .getId()
                                            .equals(
                                                    user.getId()
                                            )
                            ) {

                                throw new IllegalArgumentException(
                                        "This email address is already in use."
                                );
                            }
                        }
                );


        user.setFirstName(
                request
                        .getFirstName()
                        .trim()
        );


        user.setLastName(
                request
                        .getLastName()
                        .trim()
        );


        user.setEmail(
                email
        );


        User savedUser =
                userRepository.save(
                        user
                );


        return ResponseEntity.ok(
                toProfileResponse(
                        savedUser
                )
        );
    }


    // ==================================================
    // ŞİFRE DEĞİŞTİR
    // ==================================================

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>>
    changePassword(
            @RequestBody
            ChangePasswordRequest request,

            Authentication authentication
    ) {

        CustomUserPrincipal principal =
                (CustomUserPrincipal)
                        authentication
                                .getPrincipal();


        User user =
                userRepository
                        .findById(
                                principal.getId()
                        )
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "User not found."
                                        )
                        );


        // --------------------------------------------------
        // MEVCUT ŞİFRE KONTROLÜ
        // --------------------------------------------------

        if (
                request.getCurrentPassword() == null
                ||
                !passwordEncoder.matches(
                        request.getCurrentPassword(),
                        user.getPasswordHash()
                )
        ) {

            throw new IllegalArgumentException(
                    "Current password is incorrect."
            );
        }


        // --------------------------------------------------
        // YENİ ŞİFRE ALANLARI
        // --------------------------------------------------

        if (
                request.getNewPassword() == null
                ||
                request.getNewPasswordConfirm() == null
        ) {

            throw new IllegalArgumentException(
                    "New password is required."
            );
        }


        // --------------------------------------------------
        // ŞİFRELER EŞLEŞİYOR MU?
        // --------------------------------------------------

        if (
                !request
                        .getNewPassword()
                        .equals(
                                request
                                        .getNewPasswordConfirm()
                        )
        ) {

            throw new IllegalArgumentException(
                    "New passwords do not match."
            );
        }


        String newPassword =
                request.getNewPassword();


        // --------------------------------------------------
        // BOŞ ŞİFRE
        // --------------------------------------------------

        if (newPassword.isBlank()) {

            throw new IllegalArgumentException(
                    "New password cannot be empty."
            );
        }


        // --------------------------------------------------
        // EN AZ 8 KARAKTER
        // --------------------------------------------------

        if (newPassword.length() < 8) {

            throw new IllegalArgumentException(
                    "Password must be at least 8 characters."
            );
        }


        // --------------------------------------------------
        // EN AZ 1 HARF
        // --------------------------------------------------

        if (
                !newPassword.matches(
                        ".*[A-Za-z].*"
                )
        ) {

            throw new IllegalArgumentException(
                    "Password must contain at least one letter."
            );
        }


        // --------------------------------------------------
        // EN AZ 1 RAKAM
        // --------------------------------------------------

        if (
                !newPassword.matches(
                        ".*\\d.*"
                )
        ) {

            throw new IllegalArgumentException(
                    "Password must contain at least one number."
            );
        }


        // --------------------------------------------------
        // ESKİ ŞİFREYLE AYNI OLAMAZ
        // --------------------------------------------------

        if (
                passwordEncoder.matches(
                        newPassword,
                        user.getPasswordHash()
                )
        ) {

            throw new IllegalArgumentException(
                    "New password must be different from the current password."
            );
        }


        // --------------------------------------------------
        // ŞİFREYİ KAYDET
        // --------------------------------------------------

        user.setPasswordHash(
                passwordEncoder.encode(
                        newPassword
                )
        );


        user.setMustChangePassword(
                false
        );


        userRepository.save(
                user
        );


        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Password changed successfully."
                )
        );
    }


    // ==================================================
    // ÇIKIŞ
    // ==================================================

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>>
    logout(
            HttpServletRequest request
    ) {

        HttpSession session =
                request.getSession(
                        false
                );


        if (session != null) {

            session.invalidate();
        }


        SecurityContextHolder
                .clearContext();


        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Logged out successfully."
                )
        );
    }


    // ==================================================
    // USER -> PROFILE RESPONSE
    // ==================================================

    private ProfileResponse toProfileResponse(
            User user
    ) {

        return new ProfileResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                user.isMustChangePassword()
        );
    }
}