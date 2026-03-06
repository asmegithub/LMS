package com.EGM.LMS.repository;

import com.EGM.LMS.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    List<Payment> findByStudent_IdOrderByCreatedAtDesc(UUID studentId);
}
