package com.philadelphia.inventory.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.philadelphia.inventory.entity.PasswordResetToken;
import com.philadelphia.inventory.entity.User;
import com.philadelphia.inventory.repository.PasswordResetTokenRepository;
import com.philadelphia.inventory.repository.UserRepository;

@Service
public class PasswordResetService {

    private final UserRepository userRepository;

    private final PasswordResetTokenRepository
            passwordResetTokenRepository;

    private final PasswordEncoder passwordEncoder;

    private final JavaMailSender mailSender;

    private final SecureRandom secureRandom =
            new SecureRandom();


    @Value("${spring.mail.username}")
    private String mailFrom;


    @Value("${app.frontend.url}")
    private String frontendUrl;


    @Value("${app.password-reset.expiration-minutes}")
    private long expirationMinutes;


    public PasswordResetService(
            UserRepository userRepository,
            PasswordResetTokenRepository
                    passwordResetTokenRepository,
            PasswordEncoder passwordEncoder,
            JavaMailSender mailSender
    ) {

        this.userRepository =
                userRepository;

        this.passwordResetTokenRepository =
                passwordResetTokenRepository;

        this.passwordEncoder =
                passwordEncoder;

        this.mailSender =
                mailSender;
    }


    // ==================================================
    // ŞİFRE SIFIRLAMA TALEBİ
    // ==================================================

    @Transactional
    public void requestPasswordReset(
            String email
    ) {

        String normalizedEmail =
                email.trim();


        User user =
                userRepository
                        .findByEmail(
                                normalizedEmail
                        )
                        .orElse(null);


        // --------------------------------------------------
        // KULLANICI YOKSA
        // DIŞARIYA BİLGİ VERME
        // --------------------------------------------------

        if (user == null) {

            return;
        }


        // --------------------------------------------------
        // PASİF KULLANICIYA RESET MAILİ GÖNDERME
        // --------------------------------------------------

        if (!user.isActive()) {

            return;
        }


        // --------------------------------------------------
        // ESKİ RESET TOKENLARINI SİL
        // --------------------------------------------------

        passwordResetTokenRepository
                .deleteAllByUser(
                        user
                );


        // --------------------------------------------------
        // YENİ TOKEN OLUŞTUR
        // --------------------------------------------------

        String token =
                generateSecureToken();


        PasswordResetToken resetToken =
                new PasswordResetToken();


        resetToken.setUser(
                user
        );

        resetToken.setToken(
                token
        );

        resetToken.setExpiresAt(
                LocalDateTime
                        .now()
                        .plusMinutes(
                                expirationMinutes
                        )
        );

        resetToken.setCreatedAt(
                LocalDateTime.now()
        );

        resetToken.setUsed(
                false
        );


        passwordResetTokenRepository
                .save(
                        resetToken
                );


        // --------------------------------------------------
        // EMAIL GÖNDER
        // --------------------------------------------------

        sendPasswordResetEmail(
                user,
                token
        );
    }


    // ==================================================
    // ŞİFREYİ SIFIRLA
    // ==================================================

    @Transactional
    public void resetPassword(
            String token,
            String newPassword,
            String newPasswordConfirm
    ) {


        // --------------------------------------------------
        // ŞİFRELER EŞLEŞİYOR MU?
        // --------------------------------------------------

        if (
                !newPassword.equals(
                        newPasswordConfirm
                )
        ) {

            throw new IllegalArgumentException(
                    "Passwords do not match."
            );
        }


        // --------------------------------------------------
        // TOKEN BUL
        // --------------------------------------------------

        PasswordResetToken resetToken =
                passwordResetTokenRepository
                        .findByToken(
                                token
                        )
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "Reset link is invalid."
                                        )
                        );


        // --------------------------------------------------
        // TOKEN DAHA ÖNCE KULLANILDI MI?
        // --------------------------------------------------

        if (resetToken.isUsed()) {

            throw new IllegalArgumentException(
                    "Reset link is invalid."
            );
        }


        // --------------------------------------------------
        // TOKEN SÜRESİ DOLDU MU?
        // --------------------------------------------------

        if (
                resetToken
                        .getExpiresAt()
                        .isBefore(
                                LocalDateTime.now()
                        )
        ) {

            passwordResetTokenRepository
                    .delete(
                            resetToken
                    );

            throw new IllegalArgumentException(
                    "Reset link has expired."
            );
        }


        User user =
                resetToken.getUser();


        // --------------------------------------------------
        // KULLANICI TOKEN OLUŞTURULDUKTAN SONRA
        // PASİFE ALINMIŞ OLABİLİR
        // --------------------------------------------------

        if (!user.isActive()) {

            passwordResetTokenRepository
                    .delete(
                            resetToken
                    );

            throw new IllegalArgumentException(
                    "Reset link is invalid."
            );
        }


        // --------------------------------------------------
        // ŞİFRE KURALLARI
        // --------------------------------------------------

        if (
                newPassword == null
                ||
                newPassword.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "New password cannot be empty."
            );
        }


        if (newPassword.length() < 8) {

            throw new IllegalArgumentException(
                    "Password must be at least 8 characters."
            );
        }


        if (
                !newPassword.matches(
                        ".*[A-Za-z].*"
                )
        ) {

            throw new IllegalArgumentException(
                    "Password must contain at least one letter."
            );
        }


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
        // MEVCUT ŞİFREYLE AYNI OLMASIN
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


        // Reset linki üzerinden kullanıcı kendi
        // kalıcı şifresini belirlemiş olur.
        user.setMustChangePassword(
                false
        );


        userRepository.save(
                user
        );


        // --------------------------------------------------
        // TOKEN'I GEÇERSİZ HALE GETİR
        // --------------------------------------------------

        resetToken.setUsed(
                true
        );

        passwordResetTokenRepository
                .save(
                        resetToken
                );
    }


    // ==================================================
    // GÜVENLİ TOKEN ÜRET
    // ==================================================

    private String generateSecureToken() {

        byte[] randomBytes =
                new byte[32];


        secureRandom.nextBytes(
                randomBytes
        );


        return Base64
                .getUrlEncoder()
                .withoutPadding()
                .encodeToString(
                        randomBytes
                );
    }


    // ==================================================
    // RESET EMAILİ GÖNDER
    // ==================================================

    private void sendPasswordResetEmail(
            User user,
            String token
    ) {

        String resetUrl =
                frontendUrl
                        + "/reset-password?token="
                        + token;


        SimpleMailMessage message =
                new SimpleMailMessage();


        message.setFrom(
                mailFrom
        );

        message.setTo(
                user.getEmail()
        );


        message.setSubject(
                "Philadelphia Buluntu Sistemi - Şifre Sıfırlama"
        );


        message.setText(
                "Merhaba "
                        + user.getFirstName()
                        + ",\n\n"

                        + "Philadelphia Buluntu Sistemi hesabınız için "
                        + "şifre sıfırlama talebi aldık.\n\n"

                        + "Yeni şifrenizi belirlemek için "
                        + "aşağıdaki bağlantıyı kullanın:\n\n"

                        + resetUrl
                        + "\n\n"

                        + "Bu bağlantı "
                        + expirationMinutes
                        + " dakika boyunca geçerlidir.\n\n"

                        + "Bu talebi siz oluşturmadıysanız "
                        + "bu e-postayı dikkate almayabilirsiniz.\n\n"

                        + "Philadelphia Buluntu Sistemi"
        );


        mailSender.send(
                message
        );
    }
}