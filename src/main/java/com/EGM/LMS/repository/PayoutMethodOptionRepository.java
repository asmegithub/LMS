package com.EGM.LMS.repository;

import com.EGM.LMS.model.PayoutMethodOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PayoutMethodOptionRepository extends JpaRepository<PayoutMethodOption, UUID> {
    List<PayoutMethodOption> findByIsActiveTrueOrderByNameAsc();
}

