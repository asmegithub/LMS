package com.EGM.LMS.service;

import com.EGM.LMS.dto.PaymentDTO;

import java.util.List;
import java.util.UUID;

public interface PaymentService {
    PaymentDTO createPayment(PaymentDTO payment);
    List<PaymentDTO> getAllPayments();
    PaymentDTO getPayment(UUID paymentId);
    PaymentDTO updatePayment(UUID paymentId, PaymentDTO payment);
    void deletePayment(UUID paymentId);
}
