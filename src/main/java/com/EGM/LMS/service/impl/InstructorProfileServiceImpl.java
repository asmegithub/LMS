package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.InstructorProfileDTO;
import com.EGM.LMS.dto.UserDTO;
import com.EGM.LMS.model.InstructorProfile;
import com.EGM.LMS.repository.InstructorProfileRepository;
import com.EGM.LMS.repository.UserRepository;
import com.EGM.LMS.service.InstructorProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class InstructorProfileServiceImpl implements InstructorProfileService {
    private final InstructorProfileRepository instructorProfileRepository;
    private final UserRepository userRepository;

    @Override
    public InstructorProfileDTO applyInstructorProfile(InstructorProfileDTO instructorProfile, String userEmail) {
        if (userEmail == null || userEmail.isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "User authentication required.");
        }

        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found."));

        var profile = instructorProfileRepository.findFirstByUser_Id(user.getId())
                .orElseGet(() -> InstructorProfile.builder()
                        .user(user)
                        .totalStudents(0)
                        .totalCourses(0)
                        .totalRevenue(BigDecimal.ZERO)
                        .averageRating(BigDecimal.ZERO)
                        .isVerified(false)
                        .build());

        profile.setHeadline(instructorProfile.getHeadline());
        profile.setHeadlineAm(instructorProfile.getHeadlineAm());
        profile.setHeadlineOm(instructorProfile.getHeadlineOm());
        profile.setHeadlineGz(instructorProfile.getHeadlineGz());
        profile.setBiography(instructorProfile.getBiography());
        profile.setBiographyAm(instructorProfile.getBiographyAm());
        profile.setBiographyOm(instructorProfile.getBiographyOm());
        profile.setBiographyGz(instructorProfile.getBiographyGz());
        profile.setExpertise(instructorProfile.getExpertise());
        profile.setSocialLinks(instructorProfile.getSocialLinks());
        profile.setVerified(false);
        profile.setVerifiedAt(null);

        return toDto(instructorProfileRepository.save(profile));
    }

    @Override
    public InstructorProfileDTO getMyInstructorProfile(String userEmail) {
        if (userEmail == null || userEmail.isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "User authentication required.");
        }

        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found."));

        var profile = instructorProfileRepository.findFirstByUser_Id(user.getId()).orElse(null);
        if (profile == null) {
            return null;
        }

        return toDto(profile);
    }

    @Override
    public List<InstructorProfileDTO> getPendingInstructorProfiles() {
        var profiles = instructorProfileRepository.findAll().stream()
                .filter(profile -> !profile.isVerified())
                .toList();

        var profileDtos = new java.util.ArrayList<InstructorProfileDTO>();
        for (InstructorProfile profile : profiles) {
            profileDtos.add(toDto(profile));
        }
        return profileDtos;
    }

    @Override
    public InstructorProfileDTO verifyInstructorProfile(UUID instructorProfileId, boolean verified) {
        var profile = instructorProfileRepository.findById(instructorProfileId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Instructor profile not found."));

        profile.setVerified(verified);
        profile.setVerifiedAt(verified ? java.time.LocalDateTime.now() : null);

        var user = profile.getUser();
        if (user != null) {
            user.setRole(verified ? "INSTRUCTOR" : "STUDENT");
            userRepository.save(user);
        }

        return toDto(instructorProfileRepository.save(profile));
    }

    @Override
    public InstructorProfileDTO createInstructorProfile(InstructorProfileDTO instructorProfile) {
        return toDto(instructorProfileRepository.save(toEntity(instructorProfile)));
    }

    @Override
    public List<InstructorProfileDTO> getAllInstructorProfiles() {
        var profiles = instructorProfileRepository.findAll();
        var profileDtos = new java.util.ArrayList<InstructorProfileDTO>();
        for (InstructorProfile profile : profiles) {
            profileDtos.add(toDto(profile));
        }
        return profileDtos;
    }

    @Override
    public InstructorProfileDTO getInstructorProfile(UUID instructorProfileId) {
        return toDto(instructorProfileRepository.findById(instructorProfileId).orElseThrow());
    }

    @Override
    public InstructorProfileDTO updateInstructorProfile(UUID instructorProfileId, InstructorProfileDTO instructorProfile) {
        instructorProfileRepository.findById(instructorProfileId).orElseThrow();
        var entity = toEntity(instructorProfile);
        entity.setId(instructorProfileId);
        return toDto(instructorProfileRepository.save(entity));
    }

    @Override
    public void deleteInstructorProfile(UUID instructorProfileId) {
        instructorProfileRepository.deleteById(instructorProfileId);
    }

    private InstructorProfile toEntity(InstructorProfileDTO instructorProfile) {
        var userId = instructorProfile.getUser() != null ? instructorProfile.getUser().getId() : null;
        return InstructorProfile.builder()
                .user(userId != null ? userRepository.findById(userId).orElse(null) : null)
                .headline(instructorProfile.getHeadline())
                .headlineAm(instructorProfile.getHeadlineAm())
                .headlineOm(instructorProfile.getHeadlineOm())
                .headlineGz(instructorProfile.getHeadlineGz())
                .biography(instructorProfile.getBiography())
                .biographyAm(instructorProfile.getBiographyAm())
                .biographyOm(instructorProfile.getBiographyOm())
                .biographyGz(instructorProfile.getBiographyGz())
                .expertise(instructorProfile.getExpertise())
                .socialLinks(instructorProfile.getSocialLinks())
                .totalStudents(instructorProfile.getTotalStudents() != null ? instructorProfile.getTotalStudents() : 0)
                .totalCourses(instructorProfile.getTotalCourses() != null ? instructorProfile.getTotalCourses() : 0)
                .totalRevenue(instructorProfile.getTotalRevenue() != null ? instructorProfile.getTotalRevenue() : BigDecimal.ZERO)
                .averageRating(instructorProfile.getAverageRating() != null ? instructorProfile.getAverageRating() : BigDecimal.ZERO)
                .isVerified(Boolean.TRUE.equals(instructorProfile.getIsVerified()))
                .verifiedAt(instructorProfile.getVerifiedAt())
                .build();
    }

    private InstructorProfileDTO toDto(InstructorProfile instructorProfile) {
        return InstructorProfileDTO.builder()
                .id(instructorProfile.getId())
                .user(instructorProfile.getUser() != null ? UserDTO.builder()
                                                                        .id(instructorProfile.getUser().getId())
                                                                        .firstName(instructorProfile.getUser().getFirstName())
                                                                        .lastName(instructorProfile.getUser().getLastName())
                                                                        .email(instructorProfile.getUser().getEmail())
                                                                        .profileImage(instructorProfile.getUser().getProfileImage())
                                                                        .build() 
                                                                        : null)
                .headline(instructorProfile.getHeadline())
                .headlineAm(instructorProfile.getHeadlineAm())
                .headlineOm(instructorProfile.getHeadlineOm())
                .headlineGz(instructorProfile.getHeadlineGz())
                .biography(instructorProfile.getBiography())
                .biographyAm(instructorProfile.getBiographyAm())
                .biographyOm(instructorProfile.getBiographyOm())
                .biographyGz(instructorProfile.getBiographyGz())
                .expertise(instructorProfile.getExpertise())
                .socialLinks(instructorProfile.getSocialLinks())
                .totalStudents(instructorProfile.getTotalStudents())
                .totalCourses(instructorProfile.getTotalCourses())
                .totalRevenue(instructorProfile.getTotalRevenue())
                .averageRating(instructorProfile.getAverageRating())
                .isVerified(instructorProfile.isVerified())
                .verifiedAt(instructorProfile.getVerifiedAt())
                .createdAt(instructorProfile.getCreatedAt())
                .updatedAt(instructorProfile.getUpdatedAt())
                .build();
    }
}
