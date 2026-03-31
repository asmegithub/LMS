package com.EGM.LMS.service;

import com.EGM.LMS.dto.ReferralBalanceDTO;
import com.EGM.LMS.dto.WithdrawalRequestDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReferralBalanceService {

    ReferralBalanceDTO getMyBalance();

    void creditReferrer(UUID referrerId, UUID enrollmentId, BigDecimal amount);

    /** Uses referral balance to pay for course; returns payment id if successful. */
    Optional<UUID> useBalanceForEnrollment(UUID studentId, UUID courseId, BigDecimal amount);

    WithdrawalRequestDTO requestWithdrawal(BigDecimal amount, UUID methodOptionId, String payoutDetailsJson);

    WithdrawalRequestDTO resubmitWithdrawal(UUID requestId, BigDecimal amount, UUID methodOptionId, String payoutDetailsJson);

    List<WithdrawalRequestDTO> getMyWithdrawals();

    WithdrawalRequestDTO getWithdrawalById(UUID requestId);

    List<WithdrawalRequestDTO> getPendingWithdrawalsForAdmin();

    WithdrawalRequestDTO reportWithdrawalReceiptIssue(UUID requestId, String message);

    WithdrawalRequestDTO approveWithdrawal(UUID requestId, String receiptStoredFileName, String receiptOriginalFileName);

    WithdrawalRequestDTO rejectWithdrawal(UUID requestId, String reason);
}
