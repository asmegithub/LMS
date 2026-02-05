package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.UserDTO;
import com.EGM.LMS.model.User;
import com.EGM.LMS.repository.UserRepository;
import com.EGM.LMS.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDTO createUser(UserDTO user) {
        return toDto(userRepository.save(toEntity(user)));
    }

    @Override
    public List<UserDTO> getAllUsers() {
        var users = userRepository.findAll();
        var userDtos = new java.util.ArrayList<UserDTO>();
        for (User user : users) {
            userDtos.add(toDto(user));
        }
        return userDtos;
    }

    @Override
    public UserDTO getUser(UUID userId) {
        return toDto(userRepository.findById(userId).orElseThrow());
    }

    @Override
    public UserDTO updateUser(UUID userId, UserDTO user) {
        userRepository.findById(userId).orElseThrow();
        var entity = toEntity(user);
        entity.setId(userId);
        return toDto(userRepository.save(entity));
    }

    @Override
    public void deleteUser(UUID userId) {
        userRepository.deleteById(userId);
    }

    private User toEntity(UserDTO user) {
        return User.builder()
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
                .build();
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
