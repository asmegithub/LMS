package com.EGM.LMS.repository;

import com.EGM.LMS.model.PaymentProof;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentProofRepository extends JpaRepository<PaymentProof, UUID> {
    List<PaymentProof> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    List<PaymentProof> findByStudent_IdOrderByCreatedAtDesc(UUID studentId);
}

