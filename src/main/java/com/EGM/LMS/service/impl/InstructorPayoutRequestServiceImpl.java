package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.InstructorBankDetailDTO;
import com.EGM.LMS.dto.InstructorProfileDTO;
import com.EGM.LMS.dto.InstructorPayoutRequestDTO;
import com.EGM.LMS.model.InstructorBankDetail;
import com.EGM.LMS.model.InstructorPayoutRequest;
import com.EGM.LMS.repository.InstructorBankDetailRepository;
import com.EGM.LMS.repository.InstructorEarningRepository;
import com.EGM.LMS.repository.InstructorPayoutRequestRepository;
import com.EGM.LMS.repository.InstructorProfileRepository;
import com.EGM.LMS.repository.UserRepository;
import com.EGM.LMS.service.InstructorPayoutRequestService;
import com.EGM.LMS.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InstructorPayoutRequestServiceImpl implements InstructorPayoutRequestService {

    private final InstructorPayoutRequestRepository payoutRequestRepository;
    private final InstructorProfileRepository instructorProfileRepository;
    private final InstructorBankDetailRepository bankDetailRepository;
    private final InstructorEarningRepository earningRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public InstructorPayoutRequestDTO requestPayout(BigDecimal amount, UUID bankDetailId) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) throw new IllegalStateException("Not authenticated");
        var user = userRepository.findByEmail(auth.getName()).orElseThrow(() -> new IllegalStateException("User not found"));
        var profile = instructorProfileRepository.findFirstByUser_Id(user.getId()).orElseThrow(() -> new IllegalStateException("Instructor profile not found"));
        var bankDetail = bankDetailId != null ? bankDetailRepository.findById(bankDetailId).orElse(null) : null;
        if (bankDetail != null && !bankDetail.getInstructorProfile().getId().equals(profile.getId()))
            throw new IllegalArgumentException("Bank detail does not belong to instructor");
        var earning = earningRepository.findFirstByInstructorProfile_Id(profile.getId()).orElse(null);
        var available = earning != null && earning.getCurrentBalance() != null ? earning.getCurrentBalance() : BigDecimal.ZERO;
        if (amount.compareTo(available) > 0) throw new IllegalArgumentException("Insufficient balance");
        if (amount.compareTo(BigDecimal.valueOf(100)) < 0) throw new IllegalArgumentException("Minimum withdrawal is 100 ETB");
        var request = InstructorPayoutRequest.builder()
                .instructorProfile(profile)
                .bankDetail(bankDetail)
                .amount(amount)
                .status("PENDING")
                .build();
        request = payoutRequestRepository.save(request);
        notificationService.notifyAdmins(
                "INSTRUCTOR_PAYOUT_REQUEST",
                "Instructor payout request",
                "An instructor requested payout: ETB " + amount + " (" + user.getEmail() + ")",
                "InstructorPayoutRequest",
                request.getId().toString(),
                "/admin/payouts"
        );
        return toDto(request);
    }

    @Override
    public List<InstructorPayoutRequestDTO> getMyPayoutRequests() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) return List.of();
        var user = userRepository.findByEmail(auth.getName());
        if (user.isEmpty()) return List.of();
        var profile = instructorProfileRepository.findFirstByUser_Id(user.get().getId());
        if (profile.isEmpty()) return List.of();
        var list = payoutRequestRepository.findByInstructorProfile_IdOrderByCreatedAtDesc(profile.get().getId());
        var dtos = new ArrayList<InstructorPayoutRequestDTO>();
        for (InstructorPayoutRequest r : list) dtos.add(toDto(r));
        return dtos;
    }

    private InstructorPayoutRequestDTO toDto(InstructorPayoutRequest r) {
        return InstructorPayoutRequestDTO.builder()
                .id(r.getId())
                .instructorProfile(r.getInstructorProfile() != null ? InstructorProfileDTO.builder().id(r.getInstructorProfile().getId()).build() : null)
                .bankDetail(r.getBankDetail() != null ? toBankDto(r.getBankDetail()) : null)
                .amount(r.getAmount())
                .status(r.getStatus())
                .createdAt(r.getCreatedAt())
                .build();
    }

    private InstructorBankDetailDTO toBankDto(InstructorBankDetail b) {
        return InstructorBankDetailDTO.builder()
                .id(b.getId())
                .bankName(b.getBankName())
                .accountName(b.getAccountName())
                .accountNumber(b.getAccountNumber())
                .branchName(b.getBranchName())
                .build();
    }
}
