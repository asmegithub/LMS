package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.*;
import com.EGM.LMS.model.*;
import com.EGM.LMS.repository.*;
import com.EGM.LMS.service.EmailLogService;
import com.EGM.LMS.service.EnrollmentService;
import com.EGM.LMS.service.NotificationService;
import com.EGM.LMS.service.PaymentProofService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentProofServiceImpl implements PaymentProofService {

    private final PaymentProofRepository paymentProofRepository;
    private final PaymentAccountRepository paymentAccountRepository;
    private final CourseRepository courseRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final EnrollmentService enrollmentService;
    private final NotificationService notificationService;
    private final EmailLogService emailLogService;

    @Override
    @Transactional
    public PaymentProofDTO submitForCourse(UUID courseId, UUID paymentAccountId, BigDecimal amount, String currency, String storedFileName, String originalFileName, String note) {
        var student = resolveAuthenticatedUser();
        if (!"STUDENT".equalsIgnoreCase(student.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only students can submit payment proofs");
        }
        if (courseId == null) throw new IllegalArgumentException("courseId is required");
        if (paymentAccountId == null) throw new IllegalArgumentException("paymentAccountId is required");
        if (storedFileName == null || storedFileName.isBlank()) throw new IllegalArgumentException("receipt file is required");

        Course course = courseRepository.findById(courseId).orElseThrow(() -> new IllegalArgumentException("Course not found"));
        PaymentAccount account = paymentAccountRepository.findById(paymentAccountId).orElseThrow(() -> new IllegalArgumentException("Payment account not found"));
        if (Boolean.FALSE.equals(account.getIsActive())) {
            throw new IllegalArgumentException("Payment account is not active");
        }
        BigDecimal safeAmount = amount != null ? amount : BigDecimal.ZERO;
        String safeCurrency = (currency != null && !currency.isBlank()) ? currency : (course.getCurrency() != null ? course.getCurrency() : "ETB");

        PaymentProof proof = PaymentProof.builder()
                .student(student)
                .course(course)
                .paymentAccount(account)
                .amount(safeAmount)
                .currency(safeCurrency)
                .status("PENDING")
                .storedFileName(storedFileName)
                .originalFileName(originalFileName)
                .note(note)
                .build();
        proof = paymentProofRepository.save(proof);

        notificationService.notifyAdmins(
                "PAYMENT_PROOF_SUBMITTED",
                "Manual payment receipt submitted",
                "A student submitted a payment receipt for course: " + (course.getTitle() != null ? course.getTitle() : course.getId()) + " (" + student.getEmail() + ")",
                "PaymentProof",
                proof.getId().toString(),
                "/admin/payment-proofs"
        );

        return toDto(proof);
    }

    @Override
    @Transactional
    public PaymentProofDTO submitForOrder(UUID orderId, UUID paymentAccountId, BigDecimal amount, String currency, String storedFileName, String originalFileName, String note) {
        var student = resolveAuthenticatedUser();
        if (!"STUDENT".equalsIgnoreCase(student.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only students can submit payment proofs");
        }
        if (orderId == null) throw new IllegalArgumentException("orderId is required");
        if (paymentAccountId == null) throw new IllegalArgumentException("paymentAccountId is required");
        if (storedFileName == null || storedFileName.isBlank()) throw new IllegalArgumentException("receipt file is required");

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (order.getStudent() == null || order.getStudent().getId() == null || !order.getStudent().getId().equals(student.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Order does not belong to student");
        }
        PaymentAccount account = paymentAccountRepository.findById(paymentAccountId).orElseThrow(() -> new IllegalArgumentException("Payment account not found"));
        if (Boolean.FALSE.equals(account.getIsActive())) {
            throw new IllegalArgumentException("Payment account is not active");
        }

        BigDecimal safeAmount = amount != null ? amount : (order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO);
        String safeCurrency = (currency != null && !currency.isBlank()) ? currency : (order.getCurrency() != null ? order.getCurrency() : "ETB");

        PaymentProof proof = PaymentProof.builder()
                .student(student)
                .order(order)
                .paymentAccount(account)
                .amount(safeAmount)
                .currency(safeCurrency)
                .status("PENDING")
                .storedFileName(storedFileName)
                .originalFileName(originalFileName)
                .note(note)
                .build();
        proof = paymentProofRepository.save(proof);

        notificationService.notifyAdmins(
                "PAYMENT_PROOF_SUBMITTED",
                "Manual cart payment receipt submitted",
                "A student submitted a cart payment receipt (order " + orderId + ") (" + student.getEmail() + ")",
                "PaymentProof",
                proof.getId().toString(),
                "/admin/payment-proofs"
        );

        return toDto(proof);
    }

    @Override
    public List<PaymentProofDTO> getMyProofs() {
        var student = resolveAuthenticatedUser();
        return paymentProofRepository.findByStudent_IdOrderByCreatedAtDesc(student.getId()).stream().map(this::toDto).toList();
    }

    @Override
    public List<PaymentProofDTO> getPending() {
        return paymentProofRepository.findByStatusIgnoreCaseOrderByCreatedAtDesc("PENDING").stream().map(this::toDto).toList();
    }

    @Override
    public PaymentProofDTO getById(UUID proofId) {
        var viewer = resolveAuthenticatedUser();
        var proof = paymentProofRepository.findById(proofId).orElseThrow(() -> new IllegalArgumentException("Payment proof not found"));
        boolean isAdmin = "ADMIN".equalsIgnoreCase(viewer.getRole());
        boolean isOwner = proof.getStudent() != null && proof.getStudent().getId() != null && proof.getStudent().getId().equals(viewer.getId());
        if (!isAdmin && !isOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed");
        }
        return toDto(proof);
    }

    @Override
    @Transactional
    public PaymentProofDTO approve(UUID proofId) {
        var reviewer = resolveAuthenticatedUser();
        if (!"ADMIN".equalsIgnoreCase(reviewer.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can approve");
        }
        var proof = paymentProofRepository.findById(proofId).orElseThrow(() -> new IllegalArgumentException("Payment proof not found"));
        if (!"PENDING".equalsIgnoreCase(proof.getStatus())) {
            throw new IllegalStateException("Payment proof is not pending");
        }
        if (proof.getPayment() != null) {
            return toDto(proof);
        }

        // Create a COMPLETED payment (manual) and then enroll using existing logic.
        Payment payment = Payment.builder()
                .student(proof.getStudent())
                .course(proof.getCourse())
                .order(proof.getOrder())
                .amount(proof.getAmount())
                .currency(proof.getCurrency())
                .gateway("MANUAL")
                .status("COMPLETED")
                .netAmount(proof.getAmount())
                .paidAt(LocalDateTime.now())
                .gatewayReference("paymentProof:" + proof.getId())
                .build();
        payment = paymentRepository.save(payment);

        proof.setStatus("APPROVED");
        proof.setReviewer(reviewer);
        proof.setReviewedAt(LocalDateTime.now());
        proof.setPayment(payment);
        paymentProofRepository.save(proof);

        if (payment.getOrder() != null) {
            payment.getOrder().setStatus("COMPLETED");
            payment.getOrder().setPaidAt(LocalDateTime.now());
            orderRepository.save(payment.getOrder());
            enrollmentService.createEnrollmentsForOrder(payment.getOrder().getId(), payment.getId());
        } else {
            enrollmentService.createEnrollmentForPayment(payment.getId());
        }

        try {
            emailLogService.recordEmail(
                    proof.getStudent().getId(),
                    proof.getStudent().getEmail(),
                    "Payment approved",
                    "PAYMENT",
                    "SENT"
            );
        } catch (Exception ignored) {}

        return toDto(proof);
    }

    @Override
    @Transactional
    public PaymentProofDTO reject(UUID proofId, String reason) {
        var reviewer = resolveAuthenticatedUser();
        if (!"ADMIN".equalsIgnoreCase(reviewer.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can reject");
        }
        var proof = paymentProofRepository.findById(proofId).orElseThrow(() -> new IllegalArgumentException("Payment proof not found"));
        if (!"PENDING".equalsIgnoreCase(proof.getStatus())) {
            throw new IllegalStateException("Payment proof is not pending");
        }
        proof.setStatus("REJECTED");
        proof.setReviewer(reviewer);
        proof.setReviewedAt(LocalDateTime.now());
        proof.setRejectionReason(reason);
        paymentProofRepository.save(proof);

        try {
            emailLogService.recordEmail(
                    proof.getStudent().getId(),
                    proof.getStudent().getEmail(),
                    "Payment rejected",
                    "PAYMENT",
                    "SENT"
            );
        } catch (Exception ignored) {}

        return toDto(proof);
    }

    @Override
    @Transactional
    public PaymentProofDTO resubmit(UUID proofId, String storedFileName, String originalFileName, String note) {
        var student = resolveAuthenticatedUser();
        if (!"STUDENT".equalsIgnoreCase(student.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only students can resubmit");
        }
        if (storedFileName == null || storedFileName.isBlank()) {
            throw new IllegalArgumentException("receipt file is required");
        }
        var proof = paymentProofRepository.findById(proofId).orElseThrow(() -> new IllegalArgumentException("Payment proof not found"));
        if (proof.getStudent() == null || proof.getStudent().getId() == null || !proof.getStudent().getId().equals(student.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Payment proof does not belong to student");
        }
        if (!"REJECTED".equalsIgnoreCase(proof.getStatus())) {
            throw new IllegalStateException("Only rejected proofs can be resubmitted");
        }

        proof.setStoredFileName(storedFileName);
        proof.setOriginalFileName(originalFileName);
        proof.setNote(note);
        proof.setStatus("PENDING");
        proof.setReviewer(null);
        proof.setReviewedAt(null);
        proof.setRejectionReason(null);
        proof.setPayment(null);
        paymentProofRepository.save(proof);

        notificationService.notifyAdmins(
                "PAYMENT_PROOF_RESUBMITTED",
                "Manual payment receipt resubmitted",
                "A student resubmitted a payment receipt (" + student.getEmail() + ")",
                "PaymentProof",
                proof.getId().toString(),
                "/admin/payments"
        );

        return toDto(proof);
    }

    private User resolveAuthenticatedUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private PaymentProofDTO toDto(PaymentProof p) {
        String receiptUrl = "/uploads/payment-proofs/" + p.getStoredFileName();
        UserDTO studentDto = null;
        if (p.getStudent() != null) {
            studentDto = UserDTO.builder()
                    .id(p.getStudent().getId())
                    .firstName(p.getStudent().getFirstName())
                    .lastName(p.getStudent().getLastName())
                    .email(p.getStudent().getEmail())
                    .build();
        }
        CourseDTO courseDto = null;
        if (p.getCourse() != null) {
            courseDto = CourseDTO.builder()
                    .id(p.getCourse().getId())
                    .title(p.getCourse().getTitle())
                    .build();
        }
        OrderDTO orderDto = null;
        if (p.getOrder() != null) {
            var items = orderItemRepository.findByOrder_Id(p.getOrder().getId()).stream()
                    .map(item -> OrderItemDTO.builder()
                            .id(item.getId())
                            .course(item.getCourse() != null ? CourseDTO.builder()
                                    .id(item.getCourse().getId())
                                    .title(item.getCourse().getTitle())
                                    .build() : null)
                            .amount(item.getAmount())
                            .platformShare(item.getPlatformShare())
                            .instructorShare(item.getInstructorShare())
                            .build())
                    .toList();
            orderDto = OrderDTO.builder()
                    .id(p.getOrder().getId())
                    .items(items)
                    .build();
        }
        return PaymentProofDTO.builder()
                .id(p.getId())
                .student(studentDto)
                .course(courseDto)
                .order(orderDto)
                .paymentAccount(p.getPaymentAccount() != null ? PaymentAccountDTO.builder().id(p.getPaymentAccount().getId()).build() : null)
                .amount(p.getAmount())
                .currency(p.getCurrency())
                .status(p.getStatus())
                .receiptUrl(receiptUrl)
                .originalFileName(p.getOriginalFileName())
                .note(p.getNote())
                .reviewer(p.getReviewer() != null ? UserDTO.builder().id(p.getReviewer().getId()).build() : null)
                .reviewedAt(p.getReviewedAt())
                .rejectionReason(p.getRejectionReason())
                .payment(p.getPayment() != null ? PaymentDTO.builder().id(p.getPayment().getId()).build() : null)
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}

