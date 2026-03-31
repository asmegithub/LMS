package com.EGM.LMS.repository;

import com.EGM.LMS.model.WithdrawalRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface WithdrawalRequestRepository extends JpaRepository<WithdrawalRequest, UUID> {

    List<WithdrawalRequest> findByUser_IdOrderByCreatedAtDesc(UUID userId);

    List<WithdrawalRequest> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);

    @Query("select coalesce(sum(r.amount), 0) from WithdrawalRequest r where r.user.id = :userId and lower(r.status) = lower('PENDING')")
    BigDecimal sumPendingAmountByUserId(@Param("userId") UUID userId);
}
