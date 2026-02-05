package com.EGM.LMS.service;

import com.EGM.LMS.dto.PayoutDTO;

import java.util.List;
import java.util.UUID;

public interface PayoutService {
    PayoutDTO createPayout(PayoutDTO payout);
    List<PayoutDTO> getAllPayouts();
    PayoutDTO getPayout(UUID payoutId);
    PayoutDTO updatePayout(UUID payoutId, PayoutDTO payout);
    void deletePayout(UUID payoutId);
}
