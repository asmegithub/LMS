package com.EGM.LMS.service;

import com.EGM.LMS.dto.PaymentProofDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface PaymentProofService {

    /** Student: submit manual payment proof for single course checkout. */
    PaymentProofDTO submitForCourse(UUID courseId, UUID paymentAccountId, BigDecimal amount, String currency, String storedFileName, String originalFileName, String note);

    /** Student: submit manual payment proof for cart checkout (multi-course) using existing orderId. */
    PaymentProofDTO submitForOrder(UUID orderId, UUID paymentAccountId, BigDecimal amount, String currency, String storedFileName, String originalFileName, String note);

    /** Student: my submitted proofs. */
    List<PaymentProofDTO> getMyProofs();

    /** Admin: list pending proofs. */
    List<PaymentProofDTO> getPending();

    /** Admin: approve proof -> create Payment + enroll. */
    PaymentProofDTO approve(UUID proofId);

    /** Admin: reject proof (no enrollment). */
    PaymentProofDTO reject(UUID proofId, String reason);

    /** Student: resubmit a rejected proof with a new receipt file. */
    PaymentProofDTO resubmit(UUID proofId, String storedFileName, String originalFileName, String note);

    PaymentProofDTO getById(UUID proofId);
}

