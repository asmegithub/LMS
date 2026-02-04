package com.EGM.LMS.repository;

import com.EGM.LMS.model.ReferralReward;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReferralRewardRepository extends JpaRepository<ReferralReward, UUID> {
}
