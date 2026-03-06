package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.ReferralBalanceDTO;
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
    public WithdrawalRequestDTO requestWithdrawal(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount must be positive");
        }
        var user = resolveAuthenticatedUser();
        var balance = getOrCreateBalance(user.getId());
        if (balance.getBalance().compareTo(amount) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient balance");
        }

        balance.setBalance(balance.getBalance().subtract(amount).setScale(2, RoundingMode.HALF_UP));
        balance.setTotalWithdrawn(balance.getTotalWithdrawn().add(amount).setScale(2, RoundingMode.HALF_UP));
        balanceRepository.save(balance);

        var request = WithdrawalRequest.builder()
                .user(user)
                .amount(amount.setScale(2, RoundingMode.HALF_UP))
                .status("PENDING")
                .build();
        request = withdrawalRepository.save(request);

        notificationService.notifyAdmins(
                "WITHDRAWAL_REQUEST",
                "Withdrawal request",
                "A user requested withdrawal: ETB " + amount.setScale(2, RoundingMode.HALF_UP) + " (" + user.getEmail() + ")",
                "WithdrawalRequest",
                request.getId().toString(),
                "/admin/withdrawals"
        );

        var tx = ReferralCreditTransaction.builder()
                .user(user)
                .amount(amount.negate().setScale(2, RoundingMode.HALF_UP))
                .type(TYPE_WITHDRAWAL)
                .referenceId(request.getId())
                .build();
        transactionRepository.save(tx);

        return toWithdrawalDto(request);
    }

    @Override
    public List<WithdrawalRequestDTO> getMyWithdrawals() {
        var user = resolveAuthenticatedUser();
        return withdrawalRepository.findByUser_IdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toWithdrawalDto)
                .collect(Collectors.toList());
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

    private WithdrawalRequestDTO toWithdrawalDto(WithdrawalRequest r) {
        return WithdrawalRequestDTO.builder()
                .id(r.getId())
                .amount(r.getAmount())
                .status(r.getStatus())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
