package com.EGM.LMS.service;

import com.EGM.LMS.dto.InstructorPayoutRequestDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface InstructorPayoutRequestService {
    /** Instructor: create a new payout request using a payout method option + details. */
    InstructorPayoutRequestDTO requestPayout(BigDecimal amount, UUID bankDetailId, UUID methodOptionId, String payoutDetailsJson);
    List<InstructorPayoutRequestDTO> getMyPayoutRequests();

    /** Admin: list pending payout requests. */
    List<InstructorPayoutRequestDTO> getPending();

    /** Admin: approve request and upload receipt. */
    InstructorPayoutRequestDTO approve(UUID requestId, String receiptStoredFileName, String receiptOriginalFileName);

    /** Admin: reject request with reason. */
    InstructorPayoutRequestDTO reject(UUID requestId, String reason);

    /** Instructor: resubmit a rejected request (updates method + details). */
    InstructorPayoutRequestDTO resubmit(UUID requestId, BigDecimal amount, UUID methodOptionId, String payoutDetailsJson);

    InstructorPayoutRequestDTO getById(UUID requestId);
}
