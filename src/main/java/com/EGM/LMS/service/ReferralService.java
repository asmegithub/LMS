package com.EGM.LMS.service;

import com.EGM.LMS.dto.ReferralDTO;

import java.util.List;
import java.util.UUID;

public interface ReferralService {
    ReferralDTO createReferral(ReferralDTO referral);
    List<ReferralDTO> getAllReferrals();
    ReferralDTO getReferral(UUID referralId);
    ReferralDTO updateReferral(UUID referralId, ReferralDTO referral);
    void deleteReferral(UUID referralId);
}
