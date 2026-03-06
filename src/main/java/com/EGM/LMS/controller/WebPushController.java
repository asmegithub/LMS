package com.EGM.LMS.controller;

import com.EGM.LMS.dto.WebPushSubscriptionDTO;
import com.EGM.LMS.repository.UserRepository;
import com.EGM.LMS.service.WebPushService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/push")
public class WebPushController {
    private final WebPushService webPushService;
    private final UserRepository userRepository;

    @GetMapping("/public-key")
    public ResponseEntity<Map<String, String>> getPublicKey() {
        return ResponseEntity.ok(Map.of("publicKey", webPushService.getPublicKey()));
    }

    @PostMapping("/subscriptions")
    public ResponseEntity<Void> subscribe(@RequestBody WebPushSubscriptionDTO request, Principal principal) {
        var user = getCurrentAdmin(principal);
        if (request == null || request.getKeys() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid subscription payload");
        }
        webPushService.upsertSubscription(
                user,
                request.getEndpoint(),
                request.getKeys().getP256dh(),
                request.getKeys().getAuth()
        );
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/subscriptions")
    public ResponseEntity<Void> unsubscribe(@RequestParam String endpoint, Principal principal) {
        var user = getCurrentAdmin(principal);
        webPushService.removeSubscription(user, endpoint);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/test")
    public ResponseEntity<Void> sendTestPush(Principal principal, @RequestBody(required = false) Map<String, String> payload) {
        var user = getCurrentAdmin(principal);
        var title = payload != null && payload.get("title") != null ? payload.get("title") : "Test notification";
        var body = payload != null && payload.get("body") != null
                ? payload.get("body")
                : "Push notifications are working on this device.";
        webPushService.sendPushToUsers(List.of(user), title, body, "/admin/settings");
        return ResponseEntity.noContent().build();
    }

    private com.EGM.LMS.model.User getCurrentAdmin(Principal principal) {
        if (principal == null || principal.getName() == null || principal.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        var user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        var role = user.getRole();
        var isAdmin = role != null && ("ADMIN".equalsIgnoreCase(role) || "ROLE_ADMIN".equalsIgnoreCase(role));
        if (!isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin role required");
        }
        return user;
    }
}