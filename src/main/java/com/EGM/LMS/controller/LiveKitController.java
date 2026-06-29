package com.EGM.LMS.controller;

import com.EGM.LMS.dto.CourseDTO;
import com.EGM.LMS.dto.EnrollmentDTO;
import com.EGM.LMS.model.User;
import com.EGM.LMS.repository.UserRepository;
import com.EGM.LMS.service.CourseService;
import com.EGM.LMS.service.EnrollmentService;
import com.EGM.LMS.service.LiveKitTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/livekit")
@RequiredArgsConstructor
public class LiveKitController {

    private final LiveKitTokenService liveKitTokenService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final UserRepository userRepository;

    @GetMapping("/token")
    public ResponseEntity<Map<String, String>> getToken(@RequestParam UUID courseId) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = auth.getName();
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userOpt.get();
        UUID userId = user.getId();
        String role = user.getRole() != null ? user.getRole().toUpperCase() : "";
        String name = user.getFirstName() + " " + user.getLastName();

        // 1. Verify course exists
        try {
            courseService.getCourse(courseId);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

        boolean canPublish = false;
        boolean canSubscribe = true;
        boolean roomAdmin = false;

        if ("ROLE_INSTRUCTOR".equals(role) || "ROLE_ADMIN".equals(role)) {
            canPublish = true;
            roomAdmin = true;
        } else if ("ROLE_STUDENT".equals(role) || "STUDENT".equals(role)) {
            Optional<EnrollmentDTO> enrollment = enrollmentService.getMyEnrollmentByCourse(courseId);
            if (enrollment.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            canPublish = true; // Students can publish their video/audio
            roomAdmin = false;
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String roomName = "course-" + courseId.toString();
        String token = liveKitTokenService.createToken(userId.toString(), name, roomName, canPublish, canSubscribe, roomAdmin);

        return ResponseEntity.ok(Map.of("token", token));
    }
}
