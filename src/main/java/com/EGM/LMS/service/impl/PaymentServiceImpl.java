package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.CourseDTO;
import com.EGM.LMS.dto.CouponDTO;
import com.EGM.LMS.dto.PaymentDTO;
import com.EGM.LMS.dto.UserDTO;
import com.EGM.LMS.model.Payment;
import com.EGM.LMS.repository.CourseRepository;
import com.EGM.LMS.repository.CouponRepository;
import com.EGM.LMS.repository.PaymentRepository;
import com.EGM.LMS.repository.UserRepository;
import com.EGM.LMS.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final CouponRepository couponRepository;

    @Override
    public PaymentDTO createPayment(PaymentDTO payment) {
        return toDto(paymentRepository.save(toEntity(payment)));
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
