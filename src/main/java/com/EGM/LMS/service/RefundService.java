package com.EGM.LMS.service;

import com.EGM.LMS.dto.RefundDTO;

import java.util.List;
import java.util.UUID;

public interface RefundService {
    RefundDTO createRefund(RefundDTO refund);
    List<RefundDTO> getAllRefunds();
    RefundDTO getRefund(UUID refundId);
    RefundDTO updateRefund(UUID refundId, RefundDTO refund);
    void deleteRefund(UUID refundId);
}
