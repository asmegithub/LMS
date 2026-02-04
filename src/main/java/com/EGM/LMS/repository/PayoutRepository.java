package com.EGM.LMS.repository;

import com.EGM.LMS.model.Payout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PayoutRepository extends JpaRepository<Payout, UUID> {
}
