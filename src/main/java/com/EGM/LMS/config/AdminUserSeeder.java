package com.EGM.LMS.config;

import com.EGM.LMS.model.User;
import com.EGM.LMS.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Seeds a default admin user on startup if one does not already exist.
 * Runs after RbacBootstrap (Order 1000) so that roles and permissions are ready.
 *
 * Configure the initial password via the ADMIN_INITIAL_PASSWORD environment variable.
 * If not set, falls back to a secure default that must be changed immediately.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Order(2000)
public class AdminUserSeeder implements ApplicationRunner {

    private static final String ADMIN_EMAIL = "asmare.zelalem@aau.edu.et";
    private static final String ADMIN_FIRST_NAME = "Asmare";
    private static final String ADMIN_LAST_NAME = "Zelalem";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.admin-password:Admin@CourseCompass2026!}")
    private String adminInitialPassword;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.existsByEmail(ADMIN_EMAIL)) {
            log.info("[AdminUserSeeder] Admin user '{}' already exists — skipping seed.", ADMIN_EMAIL);
            return;
        }

        User admin = User.builder()
                .email(ADMIN_EMAIL)
                .firstName(ADMIN_FIRST_NAME)
                .lastName(ADMIN_LAST_NAME)
                .passwordHash(passwordEncoder.encode(adminInitialPassword))
                .role("ADMIN")
                .isVerified(true)
                .isActive(true)
                .language("en")
                .timezone("Africa/Addis_Ababa")
                .emailVerifiedAt(LocalDateTime.now())
                .build();

        userRepository.save(admin);
        log.info("[AdminUserSeeder] Admin user '{}' created successfully.", ADMIN_EMAIL);
    }
}
