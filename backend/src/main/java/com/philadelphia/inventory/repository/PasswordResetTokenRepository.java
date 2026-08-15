package com.philadelphia.inventory.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.philadelphia.inventory.entity.PasswordResetToken;
import com.philadelphia.inventory.entity.User;

@Repository
public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(
            String token
    );

    void deleteAllByUser(
            User user
    );
}