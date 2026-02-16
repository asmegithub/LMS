package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.UserDTO;
import com.EGM.LMS.dto.auth.AuthResponse;
import com.EGM.LMS.dto.auth.LoginRequest;
import com.EGM.LMS.dto.auth.RefreshRequest;
import com.EGM.LMS.dto.auth.SignupRequest;
import com.EGM.LMS.model.User;
import com.EGM.LMS.model.UserSession;
import com.EGM.LMS.repository.UserRepository;
import com.EGM.LMS.repository.UserSessionRepository;
import com.EGM.LMS.security.JwtService;
import com.EGM.LMS.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private static final String DEFAULT_ROLE = "STUDENT";

    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthResponse signup(SignupRequest request, String ipAddress, String userAgent) {
        if (request == null || request.getEmail() == null || request.getPassword() == null) {
            throw new IllegalArgumentException("Email and password are required.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists.");
        }

        var user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(normalizeRole(request.getRole()))
                .language(request.getLanguage() != null ? request.getLanguage() : "en")
                .isActive(true)
                .isVerified(false)
                .build();

        user = userRepository.save(user);
        return buildAuthResponse(user, ipAddress, userAgent);
    }

    @Override
    public AuthResponse login(LoginRequest request, String ipAddress, String userAgent) {
        if (request == null || request.getEmail() == null || request.getPassword() == null) {
            throw new IllegalArgumentException("Email and password are required.");
        }
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials."));

        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new IllegalArgumentException("Account is disabled.");
        }

        if (user.getPasswordHash() == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials.");
        }

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        return buildAuthResponse(user, ipAddress, userAgent);
    }

    @Override
    public AuthResponse refresh(RefreshRequest request) {
        if (request == null || request.getRefreshToken() == null) {
            throw new IllegalArgumentException("Refresh token is required.");
        }

        var session = userSessionRepository.findByRefreshToken(request.getRefreshToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token."));

        if (!session.isActive() || session.getExpiresAt() == null || session.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Refresh token expired.");
        }

        var user = session.getUser();
        var accessToken = jwtService.generateAccessToken(user);
        var newRefreshToken = UUID.randomUUID().toString();

        session.setToken(accessToken);
        session.setRefreshToken(newRefreshToken);
        session.setLastActiveAt(LocalDateTime.now());
        session.setExpiresAt(LocalDateTime.now().plus(jwtService.getRefreshTokenTtl()));
        userSessionRepository.save(session);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenTtlSeconds())
                .user(toDto(user))
                .build();
    }

    @Override
    public void logout(RefreshRequest request) {
        if (request == null || request.getRefreshToken() == null) {
            return;
        }
        userSessionRepository.findByRefreshToken(request.getRefreshToken())
                .ifPresent(session -> {
                    session.setActive(false);
                    userSessionRepository.save(session);
                });
    }

    @Override
    public UserDTO me(String email) {
        if (email == null) {
            throw new IllegalArgumentException("User not found.");
        }
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        return toDto(user);
    }

    @Override
    public AuthResponse oauthLogin(String email, String firstName, String lastName, String profileImage, String ipAddress, String userAgent) {
        if (email == null) {
            throw new IllegalArgumentException("Email is required.");
        }

        var user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(email)
                        .firstName(firstName)
                        .lastName(lastName)
                        .profileImage(profileImage)
                        .role(DEFAULT_ROLE)
                        .language("en")
                        .isActive(true)
                        .isVerified(true)
                        .build()));

        if (profileImage != null && (user.getProfileImage() == null || user.getProfileImage().isBlank())) {
            user.setProfileImage(profileImage);
        }
        if (firstName != null && (user.getFirstName() == null || user.getFirstName().isBlank())) {
            user.setFirstName(firstName);
        }
        if (lastName != null && (user.getLastName() == null || user.getLastName().isBlank())) {
            user.setLastName(lastName);
        }
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        return buildAuthResponse(user, ipAddress, userAgent);
    }

    private AuthResponse buildAuthResponse(User user, String ipAddress, String userAgent) {
        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = UUID.randomUUID().toString();
        var now = LocalDateTime.now();

        var session = UserSession.builder()
                .user(user)
                .token(accessToken)
                .refreshToken(refreshToken)
                .deviceType("Web")
                .deviceName("Web")
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .isActive(true)
                .expiresAt(now.plus(jwtService.getRefreshTokenTtl()))
                .lastActiveAt(now)
                .build();

        userSessionRepository.save(session);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenTtlSeconds())
                .user(toDto(user))
                .build();
    }

    private String normalizeRole(String role) {
        if (role == null) {
            return DEFAULT_ROLE;
        }
        var normalized = role.trim().toUpperCase();
        if ("INSTRUCTOR".equals(normalized) || "STUDENT".equals(normalized)) {
            return normalized;
        }
        return DEFAULT_ROLE;
    }

    private UserDTO toDto(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .isVerified(user.getIsVerified())
                .isActive(user.getIsActive())
                .profileImage(user.getProfileImage())
                .language(user.getLanguage())
                .referralCode(user.getReferralCode())
                .referredBy(user.getReferredBy())
                .bio(user.getBio())
                .timezone(user.getTimezone())
                .lastLoginAt(user.getLastLoginAt())
                .emailVerifiedAt(user.getEmailVerifiedAt())
                .phoneVerifiedAt(user.getPhoneVerifiedAt())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
