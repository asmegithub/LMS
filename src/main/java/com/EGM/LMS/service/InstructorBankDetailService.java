package com.EGM.LMS.service;

import com.EGM.LMS.dto.InstructorBankDetailDTO;

import java.util.List;
import java.util.UUID;

public interface InstructorBankDetailService {
    InstructorBankDetailDTO createInstructorBankDetail(InstructorBankDetailDTO instructorBankDetail);
    List<InstructorBankDetailDTO> getMyBankDetails();
    List<InstructorBankDetailDTO> getAllInstructorBankDetails();
    InstructorBankDetailDTO getInstructorBankDetail(UUID instructorBankDetailId);
    InstructorBankDetailDTO updateInstructorBankDetail(UUID instructorBankDetailId, InstructorBankDetailDTO instructorBankDetail);
    void deleteInstructorBankDetail(UUID instructorBankDetailId);
}
