package com.EGM.LMS.controller;

import com.EGM.LMS.dto.InstructorPayoutRequestDTO;
import com.EGM.LMS.service.InstructorPayoutRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/instructor-payouts")
public class InstructorPayoutRequestController {

    private final InstructorPayoutRequestService instructorPayoutRequestService;

    @GetMapping("/me")
    public ResponseEntity<List<InstructorPayoutRequestDTO>> getMyPayoutRequests() {
        return ResponseEntity.ok(instructorPayoutRequestService.getMyPayoutRequests());
    }

    @PostMapping("/request")
    public ResponseEntity<InstructorPayoutRequestDTO> requestPayout(@RequestBody Map<String, Object> body) {
        var amountObj = body.get("amount");
        BigDecimal amount;
        if (amountObj instanceof Number n) {
            amount = BigDecimal.valueOf(n.doubleValue());
        } else if (amountObj instanceof String s) {
            amount = new BigDecimal(s);
        } else {
            return ResponseEntity.badRequest().build();
        }
        UUID bankDetailId = null;
        if (body.get("bankDetailId") != null) {
            if (body.get("bankDetailId") instanceof String str) {
                bankDetailId = UUID.fromString(str);
            }
        }
        return ResponseEntity.ok(instructorPayoutRequestService.requestPayout(amount, bankDetailId));
    }
}
