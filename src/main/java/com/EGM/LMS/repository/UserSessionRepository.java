package com.EGM.LMS.repository;

import com.EGM.LMS.model.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {

	Optional<UserSession> findByRefreshToken(String refreshToken);

	Optional<UserSession> findByTokenAndIsActiveTrue(String token);

	List<UserSession> findByUser_IdAndIsActiveTrueAndExpiresAtAfter(UUID userId, LocalDateTime now);
}
