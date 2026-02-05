package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.UserDTO;
import com.EGM.LMS.dto.UserSessionDTO;
import com.EGM.LMS.model.UserSession;
import com.EGM.LMS.repository.UserRepository;
import com.EGM.LMS.repository.UserSessionRepository;
import com.EGM.LMS.service.UserSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserSessionServiceImpl implements UserSessionService {
    private final UserSessionRepository userSessionRepository;
    private final UserRepository userRepository;

    @Override
    public UserSessionDTO createUserSession(UserSessionDTO userSession) {
        return toDto(userSessionRepository.save(toEntity(userSession)));
    }

    @Override
    public List<UserSessionDTO> getAllUserSessions() {
        var sessions = userSessionRepository.findAll();
        var sessionDtos = new java.util.ArrayList<UserSessionDTO>();
        for (UserSession session : sessions) {
            sessionDtos.add(toDto(session));
        }
        return sessionDtos;
    }

    @Override
    public UserSessionDTO getUserSession(UUID userSessionId) {
        return toDto(userSessionRepository.findById(userSessionId).orElseThrow());
    }

    @Override
    public UserSessionDTO updateUserSession(UUID userSessionId, UserSessionDTO userSession) {
        userSessionRepository.findById(userSessionId).orElseThrow();
        var entity = toEntity(userSession);
        entity.setId(userSessionId);
        return toDto(userSessionRepository.save(entity));
    }

    @Override
    public void deleteUserSession(UUID userSessionId) {
        userSessionRepository.deleteById(userSessionId);
    }

    private UserSession toEntity(UserSessionDTO userSession) {
        var userId = userSession.getUser() != null ? userSession.getUser().getId() : null;
        return UserSession.builder()
                .user(userId != null ? userRepository.findById(userId).orElse(null) : null)
                .token(userSession.getToken())
                .refreshToken(userSession.getRefreshToken())
                .deviceType(userSession.getDeviceType())
                .deviceName(userSession.getDeviceName())
                .ipAddress(userSession.getIpAddress())
                .userAgent(userSession.getUserAgent())
                .isActive(userSession.isActive())
                .expiresAt(userSession.getExpiresAt())
                .lastActiveAt(userSession.getLastActiveAt())
                .build();
    }

    private UserSessionDTO toDto(UserSession userSession) {
        return UserSessionDTO.builder()
                .id(userSession.getId())
                .user(userSession.getUser() != null ? UserDTO.builder().id(userSession.getUser().getId()).build() : null)
                .token(userSession.getToken())
                .refreshToken(userSession.getRefreshToken())
                .deviceType(userSession.getDeviceType())
                .deviceName(userSession.getDeviceName())
                .ipAddress(userSession.getIpAddress())
                .userAgent(userSession.getUserAgent())
                .isActive(userSession.isActive())
                .expiresAt(userSession.getExpiresAt())
                .lastActiveAt(userSession.getLastActiveAt())
                .createdAt(userSession.getCreatedAt())
                .updatedAt(userSession.getUpdatedAt())
                .build();
    }
}
