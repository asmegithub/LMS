package com.EGM.LMS.service;

import com.EGM.LMS.dto.ChapaInitializeRequest;
import com.EGM.LMS.dto.ChapaInitializeResponse;
import com.EGM.LMS.dto.PaymentDTO;

import java.util.List;
import java.util.UUID;

public interface PaymentService {
    PaymentDTO createPayment(PaymentDTO payment);
    /** Initialize Chapa payment; returns checkout URL. Requires authenticated student. */
    ChapaInitializeResponse initializeChapaPayment(ChapaInitializeRequest request);
    /** Handle Chapa callback: verify, complete payment, create enrollment. */
    void handleChapaCallback(String trxRef, String refId, String status);
    List<PaymentDTO> getAllPayments();
    List<PaymentDTO> getMyPayments();
    PaymentDTO getPayment(UUID paymentId);
    PaymentDTO updatePayment(UUID paymentId, PaymentDTO payment);
    void deletePayment(UUID paymentId);
}
