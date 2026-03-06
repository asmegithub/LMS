package com.EGM.LMS.controller;

import com.EGM.LMS.dto.ReferralBalanceDTO;
import com.EGM.LMS.dto.WithdrawalRequestDTO;
import com.EGM.LMS.service.ReferralBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/referral-balance")
public class ReferralBalanceController {

    private final ReferralBalanceService referralBalanceService;

    @GetMapping("/me")
    public ResponseEntity<ReferralBalanceDTO> getMyBalance() {
        return ResponseEntity.ok(referralBalanceService.getMyBalance());
    }

    @PostMapping("/withdraw")
    public ResponseEntity<WithdrawalRequestDTO> requestWithdrawal(@RequestBody Map<String, Object> body) {
        var amountObj = body.get("amount");
        BigDecimal amount;
        if (amountObj instanceof Number n) {
            amount = BigDecimal.valueOf(n.doubleValue());
        } else if (amountObj instanceof String s) {
            amount = new BigDecimal(s);
        } else {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(referralBalanceService.requestWithdrawal(amount));
    }

    @GetMapping("/withdrawals")
    public ResponseEntity<List<WithdrawalRequestDTO>> getMyWithdrawals() {
        return ResponseEntity.ok(referralBalanceService.getMyWithdrawals());
    }
}
