package com.EGM.LMS.controller;

import com.EGM.LMS.dto.InstructorBankDetailDTO;
import com.EGM.LMS.service.InstructorBankDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/instructor-bank-details")
public class InstructorBankDetailController {
    private final InstructorBankDetailService instructorBankDetailService;

    @PostMapping
    ResponseEntity<InstructorBankDetailDTO> createInstructorBankDetail(@RequestBody InstructorBankDetailDTO instructorBankDetailDto) {
        return ResponseEntity.ok(instructorBankDetailService.createInstructorBankDetail(instructorBankDetailDto));
    }

    @GetMapping
    ResponseEntity<List<InstructorBankDetailDTO>> getAllInstructorBankDetails() {
        return ResponseEntity.ok(instructorBankDetailService.getAllInstructorBankDetails());
    }

    @GetMapping("/{instructorBankDetailId}")
    ResponseEntity<InstructorBankDetailDTO> getInstructorBankDetail(@PathVariable UUID instructorBankDetailId) {
        return ResponseEntity.ok(instructorBankDetailService.getInstructorBankDetail(instructorBankDetailId));
    }

    @PutMapping("/{instructorBankDetailId}")
    ResponseEntity<InstructorBankDetailDTO> updateInstructorBankDetail(@PathVariable UUID instructorBankDetailId, @RequestBody InstructorBankDetailDTO instructorBankDetailDto) {
        return ResponseEntity.ok(instructorBankDetailService.updateInstructorBankDetail(instructorBankDetailId, instructorBankDetailDto));
    }

    @DeleteMapping("/{instructorBankDetailId}")
    ResponseEntity<Void> deleteInstructorBankDetail(@PathVariable UUID instructorBankDetailId) {
        instructorBankDetailService.deleteInstructorBankDetail(instructorBankDetailId);
        return ResponseEntity.noContent().build();
    }
}
