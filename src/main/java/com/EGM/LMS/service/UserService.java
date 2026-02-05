package com.EGM.LMS.service;

import com.EGM.LMS.dto.UserDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDTO createUser(UserDTO user);
    List<UserDTO> getAllUsers();
    UserDTO getUser(UUID userId);
    UserDTO updateUser(UUID userId, UserDTO user);
    void deleteUser(UUID userId);
}
