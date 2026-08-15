package com.philadelphia.inventory.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.philadelphia.inventory.entity.User;
import com.philadelphia.inventory.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ----------------------------------------------------
    // TÜM KULLANICILAR
    // ----------------------------------------------------

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ----------------------------------------------------
    // ID İLE KULLANICI
    // ----------------------------------------------------

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // ----------------------------------------------------
    // EMAIL İLE KULLANICI
    // ----------------------------------------------------

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // ----------------------------------------------------
    // KULLANICI OLUŞTUR
    // ----------------------------------------------------

    public User createUser(
            User user,
            String rawPassword
    ) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException(
                    "A user with this email already exists."
            );
        }

        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException(
                    "Temporary password is required."
            );
        }

        user.setPasswordHash(
                passwordEncoder.encode(rawPassword)
        );

        user.setActive(true);

        // Admin tarafından verilen ilk şifre geçicidir.
        user.setMustChangePassword(true);

        LocalDateTime now = LocalDateTime.now();

        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        return userRepository.save(user);
    }

    // ----------------------------------------------------
    // KULLANICI GÜNCELLE
    // ----------------------------------------------------

    public User updateUser(
            Long userId,
            User updatedUser
    ) {

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found."
                        )
                );

        String newEmail = updatedUser.getEmail();

        if (newEmail == null || newEmail.isBlank()) {
            throw new IllegalArgumentException(
                    "Email is required."
            );
        }

        // Email değişiyorsa başka kullanıcı tarafından
        // kullanılıyor mu kontrol et.
        if (!existingUser.getEmail().equalsIgnoreCase(newEmail)
                && userRepository.existsByEmail(newEmail)) {

            throw new IllegalArgumentException(
                    "A user with this email already exists."
            );
        }

        existingUser.setFirstName(
                updatedUser.getFirstName()
        );

        existingUser.setLastName(
                updatedUser.getLastName()
        );

        existingUser.setEmail(
                updatedUser.getEmail()
        );

        existingUser.setRole(
                updatedUser.getRole()
        );

        existingUser.setUpdatedAt(
                LocalDateTime.now()
        );

        return userRepository.save(existingUser);
    }

    // ----------------------------------------------------
    // KULLANICIYI PASİF YAP
    // ----------------------------------------------------

    public User deactivateUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found."
                        )
                );

        if (!user.isActive()) {
            throw new IllegalArgumentException(
                    "User is already inactive."
            );
        }

        user.setActive(false);
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    // ----------------------------------------------------
    // KULLANICIYI TEKRAR AKTİF YAP
    // ----------------------------------------------------

    public User activateUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found."
                        )
                );

        if (user.isActive()) {
            throw new IllegalArgumentException(
                    "User is already active."
            );
        }

        user.setActive(true);
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }
}