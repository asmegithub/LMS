package com.EGM.LMS.service.impl;

import com.EGM.LMS.config.ChapaConfig;
import com.EGM.LMS.dto.ChapaInitializeRequest;
import com.EGM.LMS.dto.ChapaInitializeResponse;
import com.EGM.LMS.dto.CourseDTO;
import com.EGM.LMS.dto.CouponDTO;
import com.EGM.LMS.dto.OrderDTO;
import com.EGM.LMS.dto.PaymentDTO;
import com.EGM.LMS.dto.UserDTO;
import com.EGM.LMS.model.Course;
import com.EGM.LMS.model.Order;
import com.EGM.LMS.model.OrderItem;
import com.EGM.LMS.model.Payment;
import com.EGM.LMS.repository.CourseRepository;
import com.EGM.LMS.repository.CouponRepository;
import com.EGM.LMS.repository.EnrollmentRepository;
import com.EGM.LMS.repository.OrderItemRepository;
import com.EGM.LMS.repository.OrderRepository;
import com.EGM.LMS.repository.PaymentRepository;
import com.EGM.LMS.repository.UserRepository;
import com.EGM.LMS.model.User;
import com.EGM.LMS.service.ChapaService;
import com.EGM.LMS.service.EnrollmentService;
import com.EGM.LMS.service.PaymentService;
import com.EGM.LMS.service.SystemSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CouponRepository couponRepository;
    private final ChapaConfig chapaConfig;
    private final ChapaService chapaService;
    private final EnrollmentService enrollmentService;
    private final EnrollmentRepository enrollmentRepository;
    private final SystemSettingService systemSettingService;

    private BigDecimal resolvePlatformFeePercent() {
        var setting = systemSettingService.getSystemSettingByKey("PLATFORM_FEE_PERCENT")
                .or(() -> systemSettingService.getSystemSettingByKey("PLATFORM_FEE"));

        if (setting.isEmpty())
            return BigDecimal.ZERO;
        var raw = setting.get().getValue();
        if (raw == null)
            return BigDecimal.ZERO;

        try {
            var pct = new BigDecimal(raw);
            if (pct.compareTo(BigDecimal.ZERO) < 0)
                return BigDecimal.ZERO;
            if (pct.compareTo(new BigDecimal("100")) > 0)
                return new BigDecimal("100");
            return pct;
        } catch (Exception ignored) {
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal[] computeShares(BigDecimal amount) {
        if (amount == null)
            return new BigDecimal[] { BigDecimal.ZERO, BigDecimal.ZERO };
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            return new BigDecimal[] { BigDecimal.ZERO, BigDecimal.ZERO };

        var platformFeePercent = resolvePlatformFeePercent();
        var platformShare = amount
                .multiply(platformFeePercent)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

        if (platformShare.compareTo(BigDecimal.ZERO) < 0)
            platformShare = BigDecimal.ZERO;
        if (platformShare.compareTo(amount) > 0)
            platformShare = amount;

        var instructorShare = amount.subtract(platformShare);
        if (instructorShare.compareTo(BigDecimal.ZERO) < 0)
            instructorShare = BigDecimal.ZERO;

        return new BigDecimal[] { platformShare, instructorShare };
    }

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

        if (entity.getAmount() != null && (entity.getPlatformShare() == null || entity.getInstructorShare() == null)) {
            var shares = computeShares(entity.getAmount());
            entity.setPlatformShare(shares[0]);
            entity.setInstructorShare(shares[1]);
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

        List<UUID> courseIds = request.getCourseIds() != null && !request.getCourseIds().isEmpty()
                ? request.getCourseIds()
                : null;
        if (courseIds == null && request.getCourseId() != null) {
            courseIds = List.of(request.getCourseId());
        }
        if (courseIds == null || courseIds.isEmpty()) {
            throw new IllegalArgumentException("courseId or courseIds is required");
        }

        if (courseIds.size() == 1) {
            return initializeChapaPaymentSingle(student, courseIds.get(0), request);
        }
        return initializeChapaPaymentMulti(student, courseIds, request);
    }

    private ChapaInitializeResponse initializeChapaPaymentSingle(User student, UUID courseId,
            ChapaInitializeRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        assertNotAlreadyEnrolled(student.getId(), course);
        BigDecimal amount = course.getDiscountPrice() != null
                && course.getDiscountPrice().compareTo(BigDecimal.ZERO) > 0
                        ? course.getDiscountPrice()
                        : course.getPrice();
        if (amount == null)
            amount = BigDecimal.ZERO;
        String currency = course.getCurrency() != null ? course.getCurrency() : "ETB";
        String slug = request.getSlug() != null ? request.getSlug() : course.getId().toString();

        var shares = computeShares(amount);
        Payment payment = Payment.builder()
                .student(student)
                .course(course)
                .amount(amount)
                .currency(currency)
                .gateway("CHAPA")
                .status("PENDING")
                .netAmount(amount)
                .platformShare(shares[0])
                .instructorShare(shares[1])
                .referralCode(request.getReferrerId() != null ? request.getReferrerId().toString() : null)
                .build();
        payment = paymentRepository.save(payment);

        String txRef = CHAPA_TX_REF_PREFIX + payment.getId();
        String callbackUrl = chapaConfig.getCallbackBaseUrl() + "/api/payments/chapa/callback";
        String returnUrl = chapaConfig.getFrontendBaseUrl() + "/courses/" + slug + "/checkout/success?paymentId="
                + payment.getId();

        String checkoutUrl = chapaService.initializeTransaction(
                amount,
                currency,
                student.getEmail(),
                student.getFirstName(),
                student.getLastName(),
                txRef,
                callbackUrl,
                returnUrl);

        return ChapaInitializeResponse.builder()
                .checkoutUrl(checkoutUrl)
                .paymentId(payment.getId())
                .txRef(txRef)
                .build();
    }

    private ChapaInitializeResponse initializeChapaPaymentMulti(User student, List<UUID> courseIds,
            ChapaInitializeRequest request) {
        List<Course> courses = courseIds.stream()
                .distinct()
                .map(courseId -> courseRepository.findById(courseId)
                        .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseId)))
                .toList();
        for (Course course : courses) {
            assertNotAlreadyEnrolled(student.getId(), course);
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        String currency = "ETB";
        Order order = Order.builder()
                .student(student)
                .gateway("CHAPA")
                .status("PENDING")
                .build();
        order = orderRepository.save(order);

        for (Course course : courses) {
            BigDecimal lineAmount = course.getDiscountPrice() != null
                    && course.getDiscountPrice().compareTo(BigDecimal.ZERO) > 0
                            ? course.getDiscountPrice()
                            : course.getPrice();
            if (lineAmount == null)
                lineAmount = BigDecimal.ZERO;
            if (currency == null || currency.isBlank())
                currency = course.getCurrency() != null ? course.getCurrency() : "ETB";
            totalAmount = totalAmount.add(lineAmount);
            OrderItem item = OrderItem.builder()
                    .order(order)
                    .course(course)
                    .amount(lineAmount)
                    .build();
            orderItemRepository.save(item);
        }
        order.setTotalAmount(totalAmount);
        order.setCurrency(currency);
        orderRepository.save(order);

        var shares = computeShares(totalAmount);
        Payment payment = Payment.builder()
                .student(student)
                .order(order)
                .amount(totalAmount)
                .currency(currency)
                .gateway("CHAPA")
                .status("PENDING")
                .netAmount(totalAmount)
                .platformShare(shares[0])
                .instructorShare(shares[1])
                .referralCode(request.getReferrerId() != null ? request.getReferrerId().toString() : null)
                .build();
        payment = paymentRepository.save(payment);

        String txRef = CHAPA_TX_REF_PREFIX + payment.getId();
        String callbackUrl = chapaConfig.getCallbackBaseUrl() + "/api/payments/chapa/callback";
        String returnUrl = chapaConfig.getFrontendBaseUrl() + "/cart/checkout/success?paymentId=" + payment.getId();
        if (request.getSlug() != null && !request.getSlug().isBlank()) {
            returnUrl = chapaConfig.getFrontendBaseUrl() + "/" + request.getSlug().replaceFirst("^/", "")
                    + "?paymentId=" + payment.getId();
        }

        String checkoutUrl = chapaService.initializeTransaction(
                totalAmount,
                currency,
                student.getEmail(),
                student.getFirstName(),
                student.getLastName(),
                txRef,
                callbackUrl,
                returnUrl);

        return ChapaInitializeResponse.builder()
                .checkoutUrl(checkoutUrl)
                .paymentId(payment.getId())
                .txRef(txRef)
                .build();
    }

    private void assertNotAlreadyEnrolled(UUID studentId, Course course) {
        if (studentId == null || course == null || course.getId() == null)
            return;
        boolean alreadyEnrolled = enrollmentRepository
                .findFirstByStudent_IdAndCourse_Id(studentId, course.getId())
                .isPresent();
        if (alreadyEnrolled) {
            String courseLabel = course.getTitle() != null && !course.getTitle().isBlank()
                    ? course.getTitle()
                    : course.getId().toString();
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already enrolled in course: " + courseLabel);
        }
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
        if (payment.getOrder() != null) {
            payment.getOrder().setStatus("COMPLETED");
            payment.getOrder().setPaidAt(LocalDateTime.now());
            orderRepository.save(payment.getOrder());
            enrollmentService.createEnrollmentsForOrder(payment.getOrder().getId(), paymentId);
        } else {
            enrollmentService.createEnrollmentForPayment(paymentId);
        }
    }

    private User resolveAuthenticatedUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            throw new IllegalStateException("Authentication required");
        }
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    private Payment toEntity(PaymentDTO dto) {
        var studentId = dto.getStudent() != null ? dto.getStudent().getId() : null;
        var orderId = dto.getOrder() != null ? dto.getOrder().getId() : null;
        var courseId = dto.getCourse() != null ? dto.getCourse().getId() : null;
        var couponId = dto.getCoupon() != null ? dto.getCoupon().getId() : null;
        return Payment.builder()
                .transactionId(dto.getTransactionId())
                .student(studentId != null ? userRepository.findById(studentId).orElse(null) : null)
                .order(orderId != null ? orderRepository.findById(orderId).orElse(null) : null)
                .course(courseId != null ? courseRepository.findById(courseId).orElse(null) : null)
                .amount(dto.getAmount())
                .currency(dto.getCurrency())
                .gateway(dto.getGateway())
                .status(dto.getStatus())
                .netAmount(dto.getNetAmount())
                .platformShare(dto.getPlatformShare())
                .instructorShare(dto.getInstructorShare())
                .coupon(couponId != null ? couponRepository.findById(couponId).orElse(null) : null)
                .discountAmount(dto.getDiscountAmount())
                .referralCode(dto.getReferralCode())
                .referralDiscount(dto.getReferralDiscount())
                .gatewayResponse(dto.getGatewayResponse())
                .gatewayReference(dto.getGatewayReference())
                .paidAt(dto.getPaidAt())
                .build();
    }

    private PaymentDTO toDto(Payment payment) {
        return PaymentDTO.builder()
                .id(payment.getId())
                .transactionId(payment.getTransactionId())
                .student(payment.getStudent() != null ? UserDTO.builder()
                        .id(payment.getStudent().getId())
                        .firstName(payment.getStudent().getFirstName())
                        .lastName(payment.getStudent().getLastName())
                        .email(payment.getStudent().getEmail())
                        .build() : null)
                .order(payment.getOrder() != null ? OrderDTO.builder().id(payment.getOrder().getId()).build() : null)
                .course(payment.getCourse() != null ? CourseDTO.builder()
                        .id(payment.getCourse().getId())
                        .title(payment.getCourse().getTitle())
                        .build() : null)
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .gateway(payment.getGateway())
                .status(payment.getStatus())
                .netAmount(payment.getNetAmount())
                .platformShare(payment.getPlatformShare())
                .instructorShare(payment.getInstructorShare())
                .coupon(payment.getCoupon() != null ? CouponDTO.builder().id(payment.getCoupon().getId()).build()
                        : null)
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
