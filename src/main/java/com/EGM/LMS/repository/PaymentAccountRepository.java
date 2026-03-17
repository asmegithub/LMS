package com.EGM.LMS.repository;

import com.EGM.LMS.model.PaymentAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentAccountRepository extends JpaRepository<PaymentAccount, UUID> {
    List<PaymentAccount> findByIsActiveTrueOrderByProviderNameAsc();
}

