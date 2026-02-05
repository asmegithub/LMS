package com.EGM.LMS.service;

import com.EGM.LMS.dto.ReferralRewardDTO;

import java.util.List;
import java.util.UUID;

public interface ReferralRewardService {
    ReferralRewardDTO createReferralReward(ReferralRewardDTO referralReward);
    List<ReferralRewardDTO> getAllReferralRewards();
    ReferralRewardDTO getReferralReward(UUID referralRewardId);
    ReferralRewardDTO updateReferralReward(UUID referralRewardId, ReferralRewardDTO referralReward);
    void deleteReferralReward(UUID referralRewardId);
}
