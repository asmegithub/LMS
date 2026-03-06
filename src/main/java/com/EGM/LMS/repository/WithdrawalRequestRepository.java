package com.EGM.LMS.repository;

import com.EGM.LMS.model.WithdrawalRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WithdrawalRequestRepository extends JpaRepository<WithdrawalRequest, UUID> {

    List<WithdrawalRequest> findByUser_IdOrderByCreatedAtDesc(UUID userId);
}
