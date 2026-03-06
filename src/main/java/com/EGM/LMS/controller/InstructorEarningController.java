package com.EGM.LMS.controller;

import com.EGM.LMS.dto.InstructorEarningDTO;
import com.EGM.LMS.service.InstructorEarningService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/instructor-earnings")
public class InstructorEarningController {
    private final InstructorEarningService instructorEarningService;

    @PostMapping
    ResponseEntity<InstructorEarningDTO> createInstructorEarning(@RequestBody InstructorEarningDTO instructorEarningDto) {
        return ResponseEntity.ok(instructorEarningService.createInstructorEarning(instructorEarningDto));
    }

    @GetMapping("/me")
    ResponseEntity<InstructorEarningDTO> getMyEarning() {
        return instructorEarningService.getMyEarning()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping
    ResponseEntity<List<InstructorEarningDTO>> getAllInstructorEarnings() {
        return ResponseEntity.ok(instructorEarningService.getAllInstructorEarnings());
    }

    @GetMapping("/{instructorEarningId}")
    ResponseEntity<InstructorEarningDTO> getInstructorEarning(@PathVariable UUID instructorEarningId) {
        return ResponseEntity.ok(instructorEarningService.getInstructorEarning(instructorEarningId));
    }

    @PutMapping("/{instructorEarningId}")
    ResponseEntity<InstructorEarningDTO> updateInstructorEarning(@PathVariable UUID instructorEarningId, @RequestBody InstructorEarningDTO instructorEarningDto) {
        return ResponseEntity.ok(instructorEarningService.updateInstructorEarning(instructorEarningId, instructorEarningDto));
    }

    @DeleteMapping("/{instructorEarningId}")
    ResponseEntity<Void> deleteInstructorEarning(@PathVariable UUID instructorEarningId) {
        instructorEarningService.deleteInstructorEarning(instructorEarningId);
        return ResponseEntity.noContent().build();
    }
}
