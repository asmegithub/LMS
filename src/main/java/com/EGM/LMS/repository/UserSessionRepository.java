package com.EGM.LMS.repository;

import com.EGM.LMS.model.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {

	Optional<UserSession> findByRefreshToken(String refreshToken);
}
