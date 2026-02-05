package com.EGM.LMS.controller;

import com.EGM.LMS.dto.InstructorProfileDTO;
import com.EGM.LMS.service.InstructorProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/instructor-profiles")
public class InstructorProfileController {
    private final InstructorProfileService instructorProfileService;

    @PostMapping
    ResponseEntity<InstructorProfileDTO> createInstructorProfile(@RequestBody InstructorProfileDTO instructorProfileDto) {
        return ResponseEntity.ok(instructorProfileService.createInstructorProfile(instructorProfileDto));
    }

    @GetMapping
    ResponseEntity<List<InstructorProfileDTO>> getAllInstructorProfiles() {
        return ResponseEntity.ok(instructorProfileService.getAllInstructorProfiles());
    }

    @GetMapping("/{instructorProfileId}")
    ResponseEntity<InstructorProfileDTO> getInstructorProfile(@PathVariable UUID instructorProfileId) {
        return ResponseEntity.ok(instructorProfileService.getInstructorProfile(instructorProfileId));
    }

    @PutMapping("/{instructorProfileId}")
    ResponseEntity<InstructorProfileDTO> updateInstructorProfile(@PathVariable UUID instructorProfileId, @RequestBody InstructorProfileDTO instructorProfileDto) {
        return ResponseEntity.ok(instructorProfileService.updateInstructorProfile(instructorProfileId, instructorProfileDto));
    }

    @DeleteMapping("/{instructorProfileId}")
    ResponseEntity<Void> deleteInstructorProfile(@PathVariable UUID instructorProfileId) {
        instructorProfileService.deleteInstructorProfile(instructorProfileId);
        return ResponseEntity.noContent().build();
    }
}
