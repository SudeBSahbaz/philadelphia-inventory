package com.philadelphia.inventory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.philadelphia.inventory.entity.User;
import com.philadelphia.inventory.entity.enums.Role;
import com.philadelphia.inventory.repository.UserRepository;

@SpringBootApplication
public class InventoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryApplication.class, args);
    }

    @Bean
    CommandLineRunner createInitialAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {

            if (userRepository.count() > 0) {
                return;
            }

            String adminPassword = System.getenv("INITIAL_ADMIN_PASSWORD");

            if (adminPassword == null || adminPassword.isBlank()) {
                System.out.println(
                        "INITIAL_ADMIN_PASSWORD is not set. Initial admin was not created."
                );
                return;
            }

            User admin = new User();

            admin.setFirstName("Admin");
            admin.setLastName("Philadelphia");

            admin.setEmail("admin@philadelphia.local");

            admin.setPasswordHash(
                    passwordEncoder.encode(adminPassword)
            );

            admin.setRole(Role.ADMIN);
            admin.setActive(true);
            admin.setMustChangePassword(true);

            userRepository.save(admin);

            System.out.println(
                    "Initial ADMIN user created successfully."
            );
        };
    }
}