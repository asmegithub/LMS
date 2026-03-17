package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.InstructorBankDetailDTO;
import com.EGM.LMS.dto.InstructorProfileDTO;
import com.EGM.LMS.dto.InstructorPayoutRequestDTO;
import com.EGM.LMS.dto.PayoutMethodOptionDTO;
import com.EGM.LMS.dto.UserDTO;
import com.EGM.LMS.model.InstructorBankDetail;
import com.EGM.LMS.model.InstructorPayoutRequest;
import com.EGM.LMS.model.PayoutMethodOption;
import com.EGM.LMS.model.User;
import com.EGM.LMS.repository.InstructorBankDetailRepository;
import com.EGM.LMS.repository.InstructorEarningRepository;
import com.EGM.LMS.repository.InstructorPayoutRequestRepository;
import com.EGM.LMS.repository.InstructorProfileRepository;
import com.EGM.LMS.repository.PayoutMethodOptionRepository;
import com.EGM.LMS.repository.UserRepository;
import com.EGM.LMS.service.InstructorPayoutRequestService;
import com.EGM.LMS.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private final PayoutMethodOptionRepository payoutMethodOptionRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public InstructorPayoutRequestDTO requestPayout(BigDecimal amount, UUID bankDetailId, UUID methodOptionId, String payoutDetailsJson) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) throw new IllegalStateException("Not authenticated");
        var user = userRepository.findByEmail(auth.getName()).orElseThrow(() -> new IllegalStateException("User not found"));
        var profile = instructorProfileRepository.findFirstByUser_Id(user.getId()).orElseThrow(() -> new IllegalStateException("Instructor profile not found"));
        var bankDetail = bankDetailId != null ? bankDetailRepository.findById(bankDetailId).orElse(null) : null;
        if (bankDetail != null && !bankDetail.getInstructorProfile().getId().equals(profile.getId()))
            throw new IllegalArgumentException("Bank detail does not belong to instructor");
        PayoutMethodOption method = null;
        if (methodOptionId != null) {
            method = payoutMethodOptionRepository.findById(methodOptionId).orElseThrow(() -> new IllegalArgumentException("Payout method not found"));
            if (Boolean.FALSE.equals(method.getIsActive())) throw new IllegalArgumentException("Payout method is not active");
        }
        var earning = earningRepository.findFirstByInstructorProfile_Id(profile.getId()).orElse(null);
        var available = earning != null && earning.getCurrentBalance() != null ? earning.getCurrentBalance() : BigDecimal.ZERO;
        var pending = payoutRequestRepository.sumPendingAmount(profile.getId());
        var remaining = available.subtract(pending != null ? pending : BigDecimal.ZERO);
        if (amount.compareTo(remaining) > 0) throw new IllegalArgumentException("Insufficient balance");
        if (amount.compareTo(BigDecimal.valueOf(100)) < 0) throw new IllegalArgumentException("Minimum withdrawal is 100 ETB");
        var request = InstructorPayoutRequest.builder()
                .instructorProfile(profile)
                .bankDetail(bankDetail)
                .methodOption(method)
                .payoutDetailsJson(payoutDetailsJson)
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

    @Override
    public List<InstructorPayoutRequestDTO> getPending() {
        requireAdmin();
        return payoutRequestRepository.findByStatusIgnoreCaseOrderByCreatedAtDesc("PENDING").stream().map(this::toDto).toList();
    }

    @Override
    @Transactional
    public InstructorPayoutRequestDTO approve(UUID requestId, String receiptStoredFileName, String receiptOriginalFileName) {
        var admin = requireAdmin();
        var req = payoutRequestRepository.findById(requestId).orElseThrow(() -> new IllegalArgumentException("Request not found"));
        if (!"PENDING".equalsIgnoreCase(req.getStatus())) throw new IllegalStateException("Request is not pending");
        req.setStatus("COMPLETED");
        req.setReviewer(admin);
        req.setReviewedAt(LocalDateTime.now());
        req.setReceiptStoredFileName(receiptStoredFileName);
        req.setReceiptOriginalFileName(receiptOriginalFileName);
        payoutRequestRepository.save(req);

        // Deduct from earnings
        var earning = earningRepository.findFirstByInstructorProfile_Id(req.getInstructorProfile().getId()).orElse(null);
        if (earning != null) {
            var current = earning.getCurrentBalance() != null ? earning.getCurrentBalance() : BigDecimal.ZERO;
            earning.setCurrentBalance(current.subtract(req.getAmount() != null ? req.getAmount() : BigDecimal.ZERO));
            var withdrawn = earning.getTotalWithdrawn() != null ? earning.getTotalWithdrawn() : BigDecimal.ZERO;
            earning.setTotalWithdrawn(withdrawn.add(req.getAmount() != null ? req.getAmount() : BigDecimal.ZERO));
            earningRepository.save(earning);
        }

        return toDto(req);
    }

    @Override
    @Transactional
    public InstructorPayoutRequestDTO reject(UUID requestId, String reason) {
        var admin = requireAdmin();
        var req = payoutRequestRepository.findById(requestId).orElseThrow(() -> new IllegalArgumentException("Request not found"));
        if (!"PENDING".equalsIgnoreCase(req.getStatus())) throw new IllegalStateException("Request is not pending");
        req.setStatus("REJECTED");
        req.setReviewer(admin);
        req.setReviewedAt(LocalDateTime.now());
        req.setRejectionReason(reason);
        payoutRequestRepository.save(req);
        return toDto(req);
    }

    @Override
    @Transactional
    public InstructorPayoutRequestDTO resubmit(UUID requestId, BigDecimal amount, UUID methodOptionId, String payoutDetailsJson) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) throw new IllegalStateException("Not authenticated");
        var user = userRepository.findByEmail(auth.getName()).orElseThrow(() -> new IllegalStateException("User not found"));
        var profile = instructorProfileRepository.findFirstByUser_Id(user.getId()).orElseThrow(() -> new IllegalStateException("Instructor profile not found"));
        var req = payoutRequestRepository.findById(requestId).orElseThrow(() -> new IllegalArgumentException("Request not found"));
        if (req.getInstructorProfile() == null || !req.getInstructorProfile().getId().equals(profile.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed");
        }
        if (!"REJECTED".equalsIgnoreCase(req.getStatus())) throw new IllegalStateException("Only rejected requests can be resubmitted");
        PayoutMethodOption method = null;
        if (methodOptionId != null) {
            method = payoutMethodOptionRepository.findById(methodOptionId).orElseThrow(() -> new IllegalArgumentException("Payout method not found"));
            if (Boolean.FALSE.equals(method.getIsActive())) throw new IllegalArgumentException("Payout method is not active");
        }
        // Re-check balance excluding pending and excluding this request since it will become pending again
        var earning = earningRepository.findFirstByInstructorProfile_Id(profile.getId()).orElse(null);
        var available = earning != null && earning.getCurrentBalance() != null ? earning.getCurrentBalance() : BigDecimal.ZERO;
        var pending = payoutRequestRepository.sumPendingAmount(profile.getId());
        var remaining = available.subtract(pending != null ? pending : BigDecimal.ZERO);
        if (amount.compareTo(remaining) > 0) throw new IllegalArgumentException("Insufficient balance");
        if (amount.compareTo(BigDecimal.valueOf(100)) < 0) throw new IllegalArgumentException("Minimum withdrawal is 100 ETB");

        req.setAmount(amount);
        req.setMethodOption(method);
        req.setPayoutDetailsJson(payoutDetailsJson);
        req.setStatus("PENDING");
        req.setRejectionReason(null);
        req.setReviewer(null);
        req.setReviewedAt(null);
        payoutRequestRepository.save(req);
        return toDto(req);
    }

    @Override
    public InstructorPayoutRequestDTO getById(UUID requestId) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) throw new IllegalStateException("Not authenticated");
        var user = userRepository.findByEmail(auth.getName()).orElseThrow(() -> new IllegalStateException("User not found"));
        var req = payoutRequestRepository.findById(requestId).orElseThrow(() -> new IllegalArgumentException("Request not found"));
        boolean isAdmin = "ADMIN".equalsIgnoreCase(user.getRole());
        boolean isOwner = req.getInstructorProfile() != null && req.getInstructorProfile().getUser() != null
                && req.getInstructorProfile().getUser().getId() != null && req.getInstructorProfile().getUser().getId().equals(user.getId());
        if (!isAdmin && !isOwner) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed");
        return toDto(req);
    }

    private InstructorPayoutRequestDTO toDto(InstructorPayoutRequest r) {
        String receiptUrl = r.getReceiptStoredFileName() != null ? "/uploads/payout-receipts/" + r.getReceiptStoredFileName() : null;
        return InstructorPayoutRequestDTO.builder()
                .id(r.getId())
                .instructorProfile(r.getInstructorProfile() != null ? InstructorProfileDTO.builder().id(r.getInstructorProfile().getId()).build() : null)
                .bankDetail(r.getBankDetail() != null ? toBankDto(r.getBankDetail()) : null)
                .methodOption(r.getMethodOption() != null ? PayoutMethodOptionDTO.builder()
                        .id(r.getMethodOption().getId())
                        .name(r.getMethodOption().getName())
                        .type(r.getMethodOption().getType())
                        .fieldsJson(r.getMethodOption().getFieldsJson())
                        .isActive(r.getMethodOption().getIsActive())
                        .build() : null)
                .payoutDetailsJson(r.getPayoutDetailsJson())
                .amount(r.getAmount())
                .status(r.getStatus())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .rejectionReason(r.getRejectionReason())
                .receiptUrl(receiptUrl)
                .receiptOriginalFileName(r.getReceiptOriginalFileName())
                .reviewer(r.getReviewer() != null ? UserDTO.builder()
                        .id(r.getReviewer().getId())
                        .firstName(r.getReviewer().getFirstName())
                        .lastName(r.getReviewer().getLastName())
                        .email(r.getReviewer().getEmail())
                        .build() : null)
                .reviewedAt(r.getReviewedAt())
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

    private User requireAdmin() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        var user = userRepository.findByEmail(auth.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        if (!"ADMIN".equalsIgnoreCase(user.getRole())) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin only");
        return user;
    }
}
