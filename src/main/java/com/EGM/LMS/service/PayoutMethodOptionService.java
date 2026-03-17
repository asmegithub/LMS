package com.EGM.LMS.service;

import com.EGM.LMS.dto.PayoutMethodOptionDTO;

import java.util.List;
import java.util.UUID;

public interface PayoutMethodOptionService {
    List<PayoutMethodOptionDTO> getActive();
    List<PayoutMethodOptionDTO> getAll();
    PayoutMethodOptionDTO create(PayoutMethodOptionDTO dto);
    PayoutMethodOptionDTO update(UUID id, PayoutMethodOptionDTO dto);
    void delete(UUID id);
}

