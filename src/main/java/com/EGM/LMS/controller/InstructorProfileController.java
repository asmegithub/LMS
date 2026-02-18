package com.EGM.LMS.controller;

import com.EGM.LMS.dto.InstructorProfileDTO;
import com.EGM.LMS.service.InstructorProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/instructor-profiles")
public class InstructorProfileController {
    private final InstructorProfileService instructorProfileService;

    @PostMapping("/apply")
    ResponseEntity<InstructorProfileDTO> applyInstructorProfile(@RequestBody InstructorProfileDTO instructorProfileDto, Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(FORBIDDEN, "Authentication required.");
        }
        return ResponseEntity.ok(instructorProfileService.applyInstructorProfile(instructorProfileDto, authentication.getName()));
    }

    @GetMapping("/me")
    ResponseEntity<InstructorProfileDTO> getMyInstructorProfile(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(FORBIDDEN, "Authentication required.");
        }
        var profile = instructorProfileService.getMyInstructorProfile(authentication.getName());
        if (profile == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/pending")
    ResponseEntity<List<InstructorProfileDTO>> getPendingInstructorProfiles(Authentication authentication) {
        requireAdmin(authentication);
        return ResponseEntity.ok(instructorProfileService.getPendingInstructorProfiles());
    }

    @PutMapping("/{instructorProfileId}/verify")
    ResponseEntity<InstructorProfileDTO> verifyInstructorProfile(
            @PathVariable UUID instructorProfileId,
            @RequestBody Map<String, Boolean> payload,
            Authentication authentication
    ) {
        requireAdmin(authentication);
        boolean verified = Boolean.TRUE.equals(payload.get("verified"));
        return ResponseEntity.ok(instructorProfileService.verifyInstructorProfile(instructorProfileId, verified));
    }

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

    private void requireAdmin(Authentication authentication) {
        if (authentication == null || authentication.getAuthorities() == null) {
            throw new ResponseStatusException(FORBIDDEN, "Admin role required.");
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));

        if (!isAdmin) {
            throw new ResponseStatusException(FORBIDDEN, "Admin role required.");
        }
    }
}
