package com.EGM.LMS.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSessionDTO {

    private UUID id;
    private UserDTO user;
    private String token;
    private String refreshToken;
    private String deviceType;
    private String deviceName;
    private String ipAddress;
    private String userAgent;
    private boolean isActive;
    private LocalDateTime expiresAt;
    private LocalDateTime lastActiveAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
