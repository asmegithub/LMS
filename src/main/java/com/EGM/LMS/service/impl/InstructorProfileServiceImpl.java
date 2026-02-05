package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.InstructorProfileDTO;
import com.EGM.LMS.dto.UserDTO;
import com.EGM.LMS.model.InstructorProfile;
import com.EGM.LMS.repository.InstructorProfileRepository;
import com.EGM.LMS.repository.UserRepository;
import com.EGM.LMS.service.InstructorProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InstructorProfileServiceImpl implements InstructorProfileService {
    private final InstructorProfileRepository instructorProfileRepository;
    private final UserRepository userRepository;

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
                .totalStudents(instructorProfile.getTotalStudents())
                .totalCourses(instructorProfile.getTotalCourses())
                .totalRevenue(instructorProfile.getTotalRevenue())
                .averageRating(instructorProfile.getAverageRating())
                .isVerified(instructorProfile.isVerified())
                .verifiedAt(instructorProfile.getVerifiedAt())
                .build();
    }

    private InstructorProfileDTO toDto(InstructorProfile instructorProfile) {
        return InstructorProfileDTO.builder()
                .id(instructorProfile.getId())
                .user(instructorProfile.getUser() != null ? UserDTO.builder().id(instructorProfile.getUser().getId()).build() : null)
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
