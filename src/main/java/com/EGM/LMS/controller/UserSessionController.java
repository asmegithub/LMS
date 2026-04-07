package com.EGM.LMS.controller;

import com.EGM.LMS.dto.UserSessionDTO;
import com.EGM.LMS.service.UserSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-sessions")
@PreAuthorize("hasAuthority('user-sessions.manage')")
public class UserSessionController {
    private final UserSessionService userSessionService;

    @PostMapping
    ResponseEntity<UserSessionDTO> createUserSession(@RequestBody UserSessionDTO userSessionDto) {
        return ResponseEntity.ok(userSessionService.createUserSession(userSessionDto));
    }

    @GetMapping
    ResponseEntity<List<UserSessionDTO>> getAllUserSessions() {
        return ResponseEntity.ok(userSessionService.getAllUserSessions());
    }

    @GetMapping("/{userSessionId}")
    ResponseEntity<UserSessionDTO> getUserSession(@PathVariable UUID userSessionId) {
        return ResponseEntity.ok(userSessionService.getUserSession(userSessionId));
    }

    @PutMapping("/{userSessionId}")
    ResponseEntity<UserSessionDTO> updateUserSession(@PathVariable UUID userSessionId, @RequestBody UserSessionDTO userSessionDto) {
        return ResponseEntity.ok(userSessionService.updateUserSession(userSessionId, userSessionDto));
    }

    @DeleteMapping("/{userSessionId}")
    ResponseEntity<Void> deleteUserSession(@PathVariable UUID userSessionId) {
        userSessionService.deleteUserSession(userSessionId);
        return ResponseEntity.noContent().build();
    }
}
