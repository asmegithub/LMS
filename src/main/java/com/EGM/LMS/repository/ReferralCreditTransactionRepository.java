package com.EGM.LMS.repository;

import com.EGM.LMS.model.ReferralCreditTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReferralCreditTransactionRepository extends JpaRepository<ReferralCreditTransaction, UUID> {

    List<ReferralCreditTransaction> findByUser_IdOrderByCreatedAtDesc(UUID userId);

    boolean existsByReferenceIdAndTypeIgnoreCase(UUID referenceId, String type);
}
