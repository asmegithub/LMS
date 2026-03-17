package com.EGM.LMS.service;

import com.EGM.LMS.dto.PaymentAccountDTO;

import java.util.List;
import java.util.UUID;

public interface PaymentAccountService {
    /** Admin: create/update/delete. */
    PaymentAccountDTO create(PaymentAccountDTO dto);
    List<PaymentAccountDTO> getAll();
    List<PaymentAccountDTO> getActive();
    PaymentAccountDTO getById(UUID id);
    PaymentAccountDTO update(UUID id, PaymentAccountDTO dto);
    void delete(UUID id);
}

