package com.EGM.LMS.service;

import com.EGM.LMS.dto.UserDTO;
import com.EGM.LMS.dto.auth.AuthResponse;
import com.EGM.LMS.dto.auth.LoginRequest;
import com.EGM.LMS.dto.auth.RefreshRequest;
import com.EGM.LMS.dto.auth.SignupRequest;

public interface AuthService {
    AuthResponse signup(SignupRequest request, String ipAddress, String userAgent);
    AuthResponse login(LoginRequest request, String ipAddress, String userAgent);
    AuthResponse refresh(RefreshRequest request);
    void logout(RefreshRequest request);
    UserDTO me(String email);
    AuthResponse oauthLogin(String email, String firstName, String lastName, String profileImage, String ipAddress, String userAgent);
}
