package com.EGM.LMS.service.impl;

import com.EGM.LMS.config.ChapaConfig;
import com.EGM.LMS.dto.ChapaInitializeRequest;
import com.EGM.LMS.dto.ChapaInitializeResponse;
import com.EGM.LMS.dto.CourseDTO;
import com.EGM.LMS.dto.CouponDTO;
import com.EGM.LMS.dto.PaymentDTO;
import com.EGM.LMS.dto.UserDTO;
import com.EGM.LMS.model.Course;
import com.EGM.LMS.model.Payment;
import com.EGM.LMS.repository.CourseRepository;
import com.EGM.LMS.repository.CouponRepository;
import com.EGM.LMS.repository.PaymentRepository;
import com.EGM.LMS.repository.UserRepository;
import com.EGM.LMS.model.User;
import com.EGM.LMS.service.ChapaService;
import com.EGM.LMS.service.EnrollmentService;
import com.EGM.LMS.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private static final String CHAPA_TX_REF_PREFIX = "lms-";

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final CouponRepository couponRepository;
    private final ChapaConfig chapaConfig;
    private final ChapaService chapaService;
    private final EnrollmentService enrollmentService;

    @Override
    public PaymentDTO createPayment(PaymentDTO payment) {
        var entity = toEntity(payment);
        if (entity.getStudent() == null) {
            entity.setStudent(resolveAuthenticatedUser());
        }
        if (entity.getPaidAt() == null && "COMPLETED".equalsIgnoreCase(entity.getStatus())) {
            entity.setPaidAt(LocalDateTime.now());
        }
        if (entity.getNetAmount() == null && entity.getAmount() != null) {
            entity.setNetAmount(entity.getAmount());
        }
        return toDto(paymentRepository.save(entity));
    }

    @Override
    public List<PaymentDTO> getAllPayments() {
        var payments = paymentRepository.findAll();
        var paymentDtos = new java.util.ArrayList<PaymentDTO>();
        for (Payment payment : payments) {
            paymentDtos.add(toDto(payment));
        }
        return paymentDtos;
    }

    @Override
    public List<PaymentDTO> getMyPayments() {
        var user = resolveAuthenticatedUser();
        var payments = paymentRepository.findByStudent_IdOrderByCreatedAtDesc(user.getId());
        var paymentDtos = new java.util.ArrayList<PaymentDTO>();
        for (Payment payment : payments) {
            paymentDtos.add(toDto(payment));
        }
        return paymentDtos;
    }

    @Override
    public PaymentDTO getPayment(UUID paymentId) {
        return toDto(paymentRepository.findById(paymentId).orElseThrow());
    }

    @Override
    public PaymentDTO updatePayment(UUID paymentId, PaymentDTO payment) {
        paymentRepository.findById(paymentId).orElseThrow();
        var entity = toEntity(payment);
        entity.setId(paymentId);
        return toDto(paymentRepository.save(entity));
    }

    @Override
    public void deletePayment(UUID paymentId) {
        paymentRepository.deleteById(paymentId);
    }

    @Override
    public ChapaInitializeResponse initializeChapaPayment(ChapaInitializeRequest request) {
        if (!chapaConfig.isEnabled()) {
            throw new IllegalStateException("Chapa is not configured. Set CHAPA_SECRET_KEY.");
        }
        User student = resolveAuthenticatedUser();
        if (!"STUDENT".equalsIgnoreCase(student.getRole())) {
            throw new IllegalStateException("Only students can initiate Chapa payment");
        }
        if (request.getCourseId() == null) {
            throw new IllegalArgumentException("courseId is required");
        }
        Course course = courseRepository.findById(request.getCourseId()).orElseThrow(() -> new IllegalArgumentException("Course not found"));
        BigDecimal amount = course.getDiscountPrice() != null && course.getDiscountPrice().compareTo(BigDecimal.ZERO) > 0
                ? course.getDiscountPrice() : course.getPrice();
        if (amount == null) amount = BigDecimal.ZERO;
        String currency = course.getCurrency() != null ? course.getCurrency() : "ETB";
        String slug = request.getSlug() != null ? request.getSlug() : course.getId().toString();

        Payment payment = Payment.builder()
                .student(student)
                .course(course)
                .amount(amount)
                .currency(currency)
                .gateway("CHAPA")
                .status("PENDING")
                .netAmount(amount)
                .referralCode(request.getReferrerId() != null ? request.getReferrerId().toString() : null)
                .build();
        payment = paymentRepository.save(payment);

        String txRef = CHAPA_TX_REF_PREFIX + payment.getId();
        String callbackUrl = chapaConfig.getCallbackBaseUrl() + "/api/payments/chapa/callback";
        String returnUrl = chapaConfig.getFrontendBaseUrl() + "/courses/" + slug + "/checkout/success?paymentId=" + payment.getId();

        String checkoutUrl = chapaService.initializeTransaction(
                amount,
                currency,
                student.getEmail(),
                student.getFirstName(),
                student.getLastName(),
                txRef,
                callbackUrl,
                returnUrl
        );

        return ChapaInitializeResponse.builder()
                .checkoutUrl(checkoutUrl)
                .paymentId(payment.getId())
                .txRef(txRef)
                .build();
    }

    @Override
    public void handleChapaCallback(String trxRef, String refId, String status) {
        if (trxRef == null || !trxRef.startsWith(CHAPA_TX_REF_PREFIX)) {
            return;
        }
        String idPart = trxRef.substring(CHAPA_TX_REF_PREFIX.length());
        UUID paymentId;
        try {
            paymentId = UUID.fromString(idPart);
        } catch (IllegalArgumentException e) {
            return;
        }
        Payment payment = paymentRepository.findById(paymentId).orElse(null);
        if (payment == null || !"PENDING".equalsIgnoreCase(payment.getStatus())) {
            return;
        }
        if (!"success".equalsIgnoreCase(status)) {
            return;
        }
        if (!chapaService.verifyTransaction(trxRef)) {
            return;
        }
        payment.setStatus("COMPLETED");
        payment.setTransactionId(refId);
        payment.setPaidAt(LocalDateTime.now());
        paymentRepository.save(payment);
        enrollmentService.createEnrollmentForPayment(paymentId);
    }

    private User resolveAuthenticatedUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            throw new IllegalStateException("Authentication required");
        }
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    private Payment toEntity(PaymentDTO payment) {
        var studentId = payment.getStudent() != null ? payment.getStudent().getId() : null;
        var courseId = payment.getCourse() != null ? payment.getCourse().getId() : null;
        var couponId = payment.getCoupon() != null ? payment.getCoupon().getId() : null;
        return Payment.builder()
                .transactionId(payment.getTransactionId())
                .student(studentId != null ? userRepository.findById(studentId).orElse(null) : null)
                .course(courseId != null ? courseRepository.findById(courseId).orElse(null) : null)
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .gateway(payment.getGateway())
                .status(payment.getStatus())
                .netAmount(payment.getNetAmount())
                .platformShare(payment.getPlatformShare())
                .instructorShare(payment.getInstructorShare())
                .coupon(couponId != null ? couponRepository.findById(couponId).orElse(null) : null)
                .discountAmount(payment.getDiscountAmount())
                .referralCode(payment.getReferralCode())
                .referralDiscount(payment.getReferralDiscount())
                .gatewayResponse(payment.getGatewayResponse())
                .gatewayReference(payment.getGatewayReference())
                .paidAt(payment.getPaidAt())
                .build();
    }

    private PaymentDTO toDto(Payment payment) {
        return PaymentDTO.builder()
                .id(payment.getId())
                .transactionId(payment.getTransactionId())
                .student(payment.getStudent() != null ? UserDTO.builder().id(payment.getStudent().getId()).build() : null)
                .course(payment.getCourse() != null ? CourseDTO.builder().id(payment.getCourse().getId()).build() : null)
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .gateway(payment.getGateway())
                .status(payment.getStatus())
                .netAmount(payment.getNetAmount())
                .platformShare(payment.getPlatformShare())
                .instructorShare(payment.getInstructorShare())
                .coupon(payment.getCoupon() != null ? CouponDTO.builder().id(payment.getCoupon().getId()).build() : null)
                .discountAmount(payment.getDiscountAmount())
                .referralCode(payment.getReferralCode())
                .referralDiscount(payment.getReferralDiscount())
                .gatewayResponse(payment.getGatewayResponse())
                .gatewayReference(payment.getGatewayReference())
                .paidAt(payment.getPaidAt())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}
