package com.EGM.LMS.service;

import java.math.BigDecimal;

/**
 * Service for Chapa payment gateway (Ethiopia).
 * Initialize transaction and verify payment.
 */
public interface ChapaService {

    /**
     * Initialize a Chapa transaction. Returns the checkout URL to redirect the customer.
     *
     * @param amount     amount to charge
     * @param currency   e.g. "ETB"
     * @param email      customer email
     * @param firstName  customer first name
     * @param lastName   customer last name
     * @param txRef      unique transaction reference (e.g. "lms-" + paymentId)
     * @param callbackUrl URL Chapa will call on payment completion
     * @param returnUrl  URL to redirect customer after payment
     * @return checkout URL to redirect the user to
     */
    String initializeTransaction(
            BigDecimal amount,
            String currency,
            String email,
            String firstName,
            String lastName,
            String txRef,
            String callbackUrl,
            String returnUrl
    );

    /**
     * Verify a transaction with Chapa using tx_ref.
     *
     * @param txRef the transaction reference used when initializing
     * @return true if verification succeeded and status is success
     */
    boolean verifyTransaction(String txRef);
}
