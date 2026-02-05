package com.EGM.LMS.service;

import com.EGM.LMS.dto.InstructorEarningDTO;

import java.util.List;
import java.util.UUID;

public interface InstructorEarningService {
    InstructorEarningDTO createInstructorEarning(InstructorEarningDTO instructorEarning);
    List<InstructorEarningDTO> getAllInstructorEarnings();
    InstructorEarningDTO getInstructorEarning(UUID instructorEarningId);
    InstructorEarningDTO updateInstructorEarning(UUID instructorEarningId, InstructorEarningDTO instructorEarning);
    void deleteInstructorEarning(UUID instructorEarningId);
}
