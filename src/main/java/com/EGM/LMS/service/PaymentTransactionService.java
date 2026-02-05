package com.EGM.LMS.service;

import com.EGM.LMS.dto.PaymentTransactionDTO;

import java.util.List;
import java.util.UUID;

public interface PaymentTransactionService {
    PaymentTransactionDTO createPaymentTransaction(PaymentTransactionDTO paymentTransaction);
    List<PaymentTransactionDTO> getAllPaymentTransactions();
    PaymentTransactionDTO getPaymentTransaction(UUID paymentTransactionId);
    PaymentTransactionDTO updatePaymentTransaction(UUID paymentTransactionId, PaymentTransactionDTO paymentTransaction);
    void deletePaymentTransaction(UUID paymentTransactionId);
}
