package com.EGM.LMS.service;

import com.EGM.LMS.dto.InstructorPayoutRequestDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface InstructorPayoutRequestService {
    InstructorPayoutRequestDTO requestPayout(BigDecimal amount, UUID bankDetailId);
    List<InstructorPayoutRequestDTO> getMyPayoutRequests();
}
