package com.EGM.LMS.controller;

import com.EGM.LMS.dto.PayoutMethodOptionDTO;
import com.EGM.LMS.service.PayoutMethodOptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payout-method-options")
public class PayoutMethodOptionController {

    private final PayoutMethodOptionService service;

    /** Instructor: list active system-wide payout methods. */
    @GetMapping("/active")
    ResponseEntity<List<PayoutMethodOptionDTO>> getActive() {
        return ResponseEntity.ok(service.getActive());
    }

    /** Admin: list all methods. */
    @GetMapping
    ResponseEntity<List<PayoutMethodOptionDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping
    ResponseEntity<PayoutMethodOptionDTO> create(@RequestBody PayoutMethodOptionDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    ResponseEntity<PayoutMethodOptionDTO> update(@PathVariable UUID id, @RequestBody PayoutMethodOptionDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

