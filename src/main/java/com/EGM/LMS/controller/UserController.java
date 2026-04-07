package com.EGM.LMS.controller;

import com.EGM.LMS.dto.UserDTO;
import com.EGM.LMS.service.AuditLogService;
import com.EGM.LMS.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@PreAuthorize("hasAuthority('users.manage')")
public class UserController {
    private final UserService userService;
    private final AuditLogService auditLogService;

    @PostMapping
    ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDto) {
        return ResponseEntity.ok(userService.createUser(userDto));
    }

    @GetMapping
    ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{userId}")
    ResponseEntity<UserDTO> getUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PutMapping("/{userId}")
    ResponseEntity<UserDTO> updateUser(@PathVariable UUID userId, @RequestBody UserDTO userDto, HttpServletRequest request) {
        UserDTO updated = userService.updateUser(userId, userDto);
        auditLogService.logAction("UPDATE_USER", "USER", userId.toString(), null, request.getRemoteAddr(), request.getHeader("User-Agent"));
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{userId}")
    ResponseEntity<Void> deleteUser(@PathVariable UUID userId, HttpServletRequest request) {
        userService.deleteUser(userId);
        auditLogService.logAction("DELETE_USER", "USER", userId.toString(), null, request.getRemoteAddr(), request.getHeader("User-Agent"));
        return ResponseEntity.noContent().build();
    }
}
