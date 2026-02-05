package com.EGM.LMS.service;

import com.EGM.LMS.dto.UserSessionDTO;

import java.util.List;
import java.util.UUID;

public interface UserSessionService {
    UserSessionDTO createUserSession(UserSessionDTO userSession);
    List<UserSessionDTO> getAllUserSessions();
    UserSessionDTO getUserSession(UUID userSessionId);
    UserSessionDTO updateUserSession(UUID userSessionId, UserSessionDTO userSession);
    void deleteUserSession(UUID userSessionId);
}
