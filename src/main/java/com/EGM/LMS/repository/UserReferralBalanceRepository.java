package com.EGM.LMS.repository;

import com.EGM.LMS.model.UserReferralBalance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserReferralBalanceRepository extends JpaRepository<UserReferralBalance, UUID> {

    Optional<UserReferralBalance> findByUser_Id(UUID userId);
}
