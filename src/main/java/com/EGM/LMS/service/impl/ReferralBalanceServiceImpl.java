package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.PayoutMethodOptionDTO;
import com.EGM.LMS.dto.ReferralBalanceDTO;
import com.EGM.LMS.dto.UserDTO;
import com.EGM.LMS.dto.WithdrawalRequestDTO;
import com.EGM.LMS.model.*;
import com.EGM.LMS.repository.*;
import com.EGM.LMS.service.NotificationService;
import com.EGM.LMS.service.ReferralBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReferralBalanceServiceImpl implements ReferralBalanceService {

    private static final String TYPE_EARNED = "EARNED";
    private static final String TYPE_USED = "USED";
    private static final String TYPE_WITHDRAWAL = "WITHDRAWAL";

    private final UserReferralBalanceRepository balanceRepository;
    private final ReferralCreditTransactionRepository transactionRepository;
    private final WithdrawalRequestRepository withdrawalRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final CourseRepository courseRepository;
    private final PayoutMethodOptionRepository payoutMethodOptionRepository;
    private final NotificationService notificationService;

    @Override
    public ReferralBalanceDTO getMyBalance() {
        var user = resolveAuthenticatedUser();
        var balance = getOrCreateBalance(user.getId());
        return ReferralBalanceDTO.builder()
                .balance(balance.getBalance())
                .totalEarned(balance.getTotalEarned())
                .totalWithdrawn(balance.getTotalWithdrawn())
                .totalUsed(balance.getTotalUsed())
                .build();
    }

    @Override
    @Transactional
    public void creditReferrer(UUID referrerId, UUID enrollmentId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) return;
        var referrer = userRepository.findById(referrerId).orElse(null);
        if (referrer == null) return;

        var balance = getOrCreateBalance(referrerId);
        balance.setBalance(balance.getBalance().add(amount).setScale(2, RoundingMode.HALF_UP));
        balance.setTotalEarned(balance.getTotalEarned().add(amount).setScale(2, RoundingMode.HALF_UP));
        balanceRepository.save(balance);

        var tx = ReferralCreditTransaction.builder()
                .user(referrer)
                .amount(amount.setScale(2, RoundingMode.HALF_UP))
                .type(TYPE_EARNED)
                .referenceId(enrollmentId)
                .build();
        transactionRepository.save(tx);
    }

    /**
     * Deducts from student's referral balance and creates a Payment with gateway=BALANCE.
     * Caller must use the returned payment id when creating the enrollment.
     */
    @Override
    @Transactional
    public Optional<UUID> useBalanceForEnrollment(UUID studentId, UUID courseId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) return Optional.empty();
        var balance = balanceRepository.findByUser_Id(studentId).orElse(null);
        if (balance == null || balance.getBalance().compareTo(amount) < 0) return Optional.empty();

        var user = userRepository.findById(studentId).orElseThrow();
        var course = courseRepository.findById(courseId).orElseThrow();

        balance.setBalance(balance.getBalance().subtract(amount).setScale(2, RoundingMode.HALF_UP));
        balance.setTotalUsed(balance.getTotalUsed().add(amount).setScale(2, RoundingMode.HALF_UP));
        balanceRepository.save(balance);

        var payment = Payment.builder()
                .student(user)
                .course(course)
                .amount(amount)
                .currency(course.getCurrency() != null ? course.getCurrency() : "ETB")
                .gateway("BALANCE")
                .status("COMPLETED")
                .netAmount(amount)
                .paidAt(LocalDateTime.now())
                .build();
        payment = paymentRepository.save(payment);

        var tx = ReferralCreditTransaction.builder()
                .user(user)
                .amount(amount.negate().setScale(2, RoundingMode.HALF_UP))
                .type(TYPE_USED)
                .referenceId(payment.getId())
                .build();
        transactionRepository.save(tx);

        return Optional.of(payment.getId());
    }

    @Override
    @Transactional
    public WithdrawalRequestDTO requestWithdrawal(BigDecimal amount, UUID methodOptionId, String payoutDetailsJson) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount must be positive");
        }
        if (methodOptionId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payout method is required");
        }
        var user = resolveAuthenticatedStudent();
        var method = payoutMethodOptionRepository.findById(methodOptionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payout method not found"));
        if (Boolean.FALSE.equals(method.getIsActive())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payout method is not active");
        }
        if (amount.compareTo(BigDecimal.valueOf(100)) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Minimum withdrawal is 100 ETB");
        }

        var balance = getOrCreateBalance(user.getId());
        var pending = withdrawalRepository.sumPendingAmountByUserId(user.getId());
        var pendingAmt = pending != null ? pending : BigDecimal.ZERO;
        var available = balance.getBalance().subtract(pendingAmt);
        if (amount.compareTo(available) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient available balance (pending withdrawals reserved)");
        }

        var request = WithdrawalRequest.builder()
                .user(user)
                .methodOption(method)
                .payoutDetailsJson(payoutDetailsJson)
                .amount(amount.setScale(2, RoundingMode.HALF_UP))
                .status("PENDING")
                .build();
        request = withdrawalRepository.save(request);

        notificationService.notifyAdmins(
                "STUDENT_REFERRAL_WITHDRAWAL",
                "Student referral withdrawal",
                user.getEmail() + " requested ETB " + amount.setScale(2, RoundingMode.HALF_UP) + " from referral balance",
                "ReferralWithdrawalRequest",
                request.getId().toString(),
                "/admin/payouts"
        );

        return toWithdrawalDto(request);
    }

    @Override
    @Transactional
    public WithdrawalRequestDTO resubmitWithdrawal(UUID requestId, BigDecimal amount, UUID methodOptionId, String payoutDetailsJson) {
        var user = resolveAuthenticatedStudent();
        var req = withdrawalRepository.findById(requestId).orElseThrow(() -> new IllegalArgumentException("Request not found"));
        if (req.getUser() == null || !req.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed");
        }
        if (!"REJECTED".equalsIgnoreCase(req.getStatus())) {
            throw new IllegalStateException("Only rejected requests can be resubmitted");
        }
        if (methodOptionId == null) throw new IllegalArgumentException("Payout method is required");
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (amount.compareTo(BigDecimal.valueOf(100)) < 0) {
            throw new IllegalArgumentException("Minimum withdrawal is 100 ETB");
        }
        var method = payoutMethodOptionRepository.findById(methodOptionId)
                .orElseThrow(() -> new IllegalArgumentException("Payout method not found"));
        if (Boolean.FALSE.equals(method.getIsActive())) throw new IllegalArgumentException("Payout method is not active");

        var balance = getOrCreateBalance(user.getId());
        var pending = withdrawalRepository.sumPendingAmountByUserId(user.getId());
        var pendingAmt = pending != null ? pending : BigDecimal.ZERO;
        var available = balance.getBalance().subtract(pendingAmt);
        if (amount.compareTo(available) > 0) throw new IllegalArgumentException("Insufficient available balance");

        req.setAmount(amount.setScale(2, RoundingMode.HALF_UP));
        req.setMethodOption(method);
        req.setPayoutDetailsJson(payoutDetailsJson);
        req.setStatus("PENDING");
        req.setRejectionReason(null);
        req.setReviewer(null);
        req.setReviewedAt(null);
        req.setReceiptStoredFileName(null);
        req.setReceiptOriginalFileName(null);
        withdrawalRepository.save(req);

        notificationService.notifyAdmins(
                "STUDENT_REFERRAL_WITHDRAWAL",
                "Student referral withdrawal (resubmitted)",
                user.getEmail() + " resubmitted withdrawal ETB " + amount.setScale(2, RoundingMode.HALF_UP),
                "ReferralWithdrawalRequest",
                req.getId().toString(),
                "/admin/payouts"
        );

        return toWithdrawalDto(req);
    }

    @Override
    public List<WithdrawalRequestDTO> getMyWithdrawals() {
        var user = resolveAuthenticatedUser();
        return withdrawalRepository.findByUser_IdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toWithdrawalDto)
                .collect(Collectors.toList());
    }

    @Override
    public WithdrawalRequestDTO getWithdrawalById(UUID requestId) {
        var viewer = resolveAuthenticatedUser();
        var req = withdrawalRepository.findById(requestId).orElseThrow(() -> new IllegalArgumentException("Request not found"));
        boolean isAdmin = "ADMIN".equalsIgnoreCase(viewer.getRole());
        boolean isOwner = req.getUser() != null && req.getUser().getId().equals(viewer.getId());
        if (!isAdmin && !isOwner) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed");
        return toWithdrawalDto(req);
    }

    @Override
    public List<WithdrawalRequestDTO> getPendingWithdrawalsForAdmin() {
        requireAdmin();
        var pending = withdrawalRepository.findByStatusIgnoreCaseOrderByCreatedAtDesc("PENDING");
        var issueReported = withdrawalRepository.findByStatusIgnoreCaseOrderByCreatedAtDesc("RECEIPT_ISSUE");
        return java.util.stream.Stream.concat(pending.stream(), issueReported.stream())
                .sorted((a, b) -> {
                    var aTs = a.getCreatedAt();
                    var bTs = b.getCreatedAt();
                    if (aTs == null && bTs == null) return 0;
                    if (aTs == null) return 1;
                    if (bTs == null) return -1;
                    return bTs.compareTo(aTs);
                })
                .map(this::toWithdrawalDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public WithdrawalRequestDTO reportWithdrawalReceiptIssue(UUID requestId, String message) {
        var student = resolveAuthenticatedStudent();
        var req = withdrawalRepository.findById(requestId).orElseThrow(() -> new IllegalArgumentException("Request not found"));
        if (req.getUser() == null || req.getUser().getId() == null || !req.getUser().getId().equals(student.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed");
        }
        if (!"COMPLETED".equalsIgnoreCase(req.getStatus())) {
            throw new IllegalStateException("Only completed withdrawals can be reported");
        }
        req.setStatus("RECEIPT_ISSUE");
        req.setReceiptIssueMessage(message != null ? message.trim() : null);
        req.setReceiptIssueReportedAt(LocalDateTime.now());
        req.setReviewedAt(null);
        req.setReviewer(null);
        withdrawalRepository.save(req);

        notificationService.notifyAdmins(
                "STUDENT_WITHDRAWAL_RECEIPT_ISSUE",
                "Student reported payout receipt issue",
                student.getEmail() + " reported a receipt issue for withdrawal " + req.getId(),
                "ReferralWithdrawalRequest",
                req.getId().toString(),
                "/admin/payouts"
        );
        return toWithdrawalDto(req);
    }

    @Override
    @Transactional
    public WithdrawalRequestDTO approveWithdrawal(UUID requestId, String receiptStoredFileName, String receiptOriginalFileName) {
        var admin = requireAdmin();
        var req = withdrawalRepository.findById(requestId).orElseThrow(() -> new IllegalArgumentException("Request not found"));
        if (!"PENDING".equalsIgnoreCase(req.getStatus()) && !"RECEIPT_ISSUE".equalsIgnoreCase(req.getStatus())) {
            throw new IllegalStateException("Request is not pending");
        }
        String finalReceiptStored = receiptStoredFileName;
        String finalReceiptOriginal = receiptOriginalFileName;
        if (finalReceiptStored == null || finalReceiptStored.isBlank()) {
            finalReceiptStored = req.getReceiptStoredFileName();
            finalReceiptOriginal = req.getReceiptOriginalFileName();
        }
        if (finalReceiptStored == null || finalReceiptStored.isBlank()) {
            throw new IllegalArgumentException("Receipt file is required");
        }

        var user = req.getUser();
        if (user == null) throw new IllegalStateException("Invalid request");
        boolean alreadyDebited = transactionRepository.existsByReferenceIdAndTypeIgnoreCase(req.getId(), TYPE_WITHDRAWAL);
        if (!alreadyDebited) {
            var balance = getOrCreateBalance(user.getId());
            if (balance.getBalance().compareTo(req.getAmount()) < 0) {
                throw new IllegalStateException("Student balance is no longer sufficient");
            }
            balance.setBalance(balance.getBalance().subtract(req.getAmount()).setScale(2, RoundingMode.HALF_UP));
            balance.setTotalWithdrawn(balance.getTotalWithdrawn().add(req.getAmount()).setScale(2, RoundingMode.HALF_UP));
            balanceRepository.save(balance);
        }

        req.setStatus("COMPLETED");
        req.setReviewer(admin);
        req.setReviewedAt(LocalDateTime.now());
        req.setReceiptStoredFileName(finalReceiptStored);
        req.setReceiptOriginalFileName(finalReceiptOriginal);
        req.setReceiptIssueMessage(null);
        req.setReceiptIssueReportedAt(null);
        withdrawalRepository.save(req);

        if (!alreadyDebited) {
            var tx = ReferralCreditTransaction.builder()
                    .user(user)
                    .amount(req.getAmount().negate().setScale(2, RoundingMode.HALF_UP))
                    .type(TYPE_WITHDRAWAL)
                    .referenceId(req.getId())
                    .build();
            transactionRepository.save(tx);
        }

        return toWithdrawalDto(req);
    }

    @Override
    @Transactional
    public WithdrawalRequestDTO rejectWithdrawal(UUID requestId, String reason) {
        var admin = requireAdmin();
        var req = withdrawalRepository.findById(requestId).orElseThrow(() -> new IllegalArgumentException("Request not found"));
        if (!"PENDING".equalsIgnoreCase(req.getStatus())) throw new IllegalStateException("Request is not pending");
        req.setStatus("REJECTED");
        req.setReviewer(admin);
        req.setReviewedAt(LocalDateTime.now());
        req.setRejectionReason(reason);
        withdrawalRepository.save(req);
        return toWithdrawalDto(req);
    }

    private UserReferralBalance getOrCreateBalance(UUID userId) {
        return balanceRepository.findByUser_Id(userId)
                .orElseGet(() -> {
                    var user = userRepository.findById(userId).orElseThrow();
                    var b = UserReferralBalance.builder()
                            .user(user)
                            .balance(BigDecimal.ZERO)
                            .totalEarned(BigDecimal.ZERO)
                            .totalWithdrawn(BigDecimal.ZERO)
                            .totalUsed(BigDecimal.ZERO)
                            .build();
                    return balanceRepository.save(b);
                });
    }

    private User resolveAuthenticatedUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private User resolveAuthenticatedStudent() {
        var user = resolveAuthenticatedUser();
        if (!"STUDENT".equalsIgnoreCase(user.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only students can withdraw referral balance");
        }
        return user;
    }

    private User requireAdmin() {
        var user = resolveAuthenticatedUser();
        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can perform this action");
        }
        return user;
    }

    private WithdrawalRequestDTO toWithdrawalDto(WithdrawalRequest r) {
        String receiptUrl = r.getReceiptStoredFileName() != null
                ? "/uploads/referral-withdrawal-receipts/" + r.getReceiptStoredFileName()
                : null;
        return WithdrawalRequestDTO.builder()
                .id(r.getId())
                .student(r.getUser() != null ? UserDTO.builder()
                        .id(r.getUser().getId())
                        .firstName(r.getUser().getFirstName())
                        .lastName(r.getUser().getLastName())
                        .email(r.getUser().getEmail())
                        .build() : null)
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
                .rejectionReason(r.getRejectionReason())
                .receiptIssueMessage(r.getReceiptIssueMessage())
                .receiptIssueReportedAt(r.getReceiptIssueReportedAt())
                .receiptUrl(receiptUrl)
                .receiptOriginalFileName(r.getReceiptOriginalFileName())
                .reviewer(r.getReviewer() != null ? UserDTO.builder()
                        .id(r.getReviewer().getId())
                        .firstName(r.getReviewer().getFirstName())
                        .lastName(r.getReviewer().getLastName())
                        .email(r.getReviewer().getEmail())
                        .build() : null)
                .reviewedAt(r.getReviewedAt())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }
}
