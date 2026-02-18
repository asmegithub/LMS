package com.EGM.LMS.service;

import com.EGM.LMS.dto.InstructorProfileDTO;

import java.util.List;
import java.util.UUID;

public interface InstructorProfileService {
    InstructorProfileDTO applyInstructorProfile(InstructorProfileDTO instructorProfile, String userEmail);
    InstructorProfileDTO getMyInstructorProfile(String userEmail);
    List<InstructorProfileDTO> getPendingInstructorProfiles();
    InstructorProfileDTO verifyInstructorProfile(UUID instructorProfileId, boolean verified);

    InstructorProfileDTO createInstructorProfile(InstructorProfileDTO instructorProfile);
    List<InstructorProfileDTO> getAllInstructorProfiles();
    InstructorProfileDTO getInstructorProfile(UUID instructorProfileId);
    InstructorProfileDTO updateInstructorProfile(UUID instructorProfileId, InstructorProfileDTO instructorProfile);
    void deleteInstructorProfile(UUID instructorProfileId);
}
