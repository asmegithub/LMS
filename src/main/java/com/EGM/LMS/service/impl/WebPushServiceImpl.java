package com.EGM.LMS.service.impl;

import com.EGM.LMS.model.User;
import com.EGM.LMS.model.WebPushSubscription;
import com.EGM.LMS.repository.WebPushSubscriptionRepository;
import com.EGM.LMS.service.WebPushService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebPushServiceImpl implements WebPushService {
    private final WebPushSubscriptionRepository webPushSubscriptionRepository;

    @Value("${app.push.vapid-public-key:}")
    private String vapidPublicKey;

    @Value("${app.push.vapid-private-key:}")
    private String vapidPrivateKey;

    @Value("${app.push.vapid-subject:mailto:admin@coursecompass.local}")
    private String vapidSubject;

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    @Override
    public String getPublicKey() {
        return vapidPublicKey == null ? "" : vapidPublicKey;
    }

    @Override
    public void upsertSubscription(User user, String endpoint, String p256dh, String auth) {
        if (user == null || endpoint == null || endpoint.isBlank()) {
            return;
        }
        if (p256dh == null || p256dh.isBlank() || auth == null || auth.isBlank()) {
            return;
        }

        var existingByEndpoint = webPushSubscriptionRepository.findAllByEndpoint(endpoint);
        if (!existingByEndpoint.isEmpty()) {
            var primary = existingByEndpoint.get(0);
            primary.setUser(user);
            primary.setP256dh(p256dh);
            primary.setAuth(auth);
            webPushSubscriptionRepository.save(primary);

            if (existingByEndpoint.size() > 1) {
                var duplicates = existingByEndpoint.subList(1, existingByEndpoint.size());
                webPushSubscriptionRepository.deleteAll(duplicates);
            }
            return;
        }

        webPushSubscriptionRepository.save(WebPushSubscription.builder()
                .user(user)
                .endpoint(endpoint)
                .p256dh(p256dh)
                .auth(auth)
                .build());
    }

    @Override
    public void removeSubscription(User user, String endpoint) {
        if (user == null || endpoint == null || endpoint.isBlank()) {
            return;
        }
        webPushSubscriptionRepository.deleteByEndpointAndUser_Id(endpoint, user.getId());
    }

    @Override
    public void sendPushToUsers(List<User> users, String title, String body, String actionUrl) {
        if (users == null || users.isEmpty()) {
            return;
        }
        if (!isConfigured()) {
            log.debug("Skipping web push send because VAPID keys are not configured.");
            return;
        }

        var userIds = users.stream().filter(Objects::nonNull).map(User::getId).filter(Objects::nonNull).toList();
        if (userIds.isEmpty()) {
            return;
        }

        var subscriptions = webPushSubscriptionRepository.findByUser_IdIn(userIds);
        if (subscriptions.isEmpty()) {
            return;
        }

        final String payloadJson = "{"
                + "\"title\":\"" + escapeJson(title) + "\"," 
                + "\"body\":\"" + escapeJson(body) + "\"," 
                + "\"url\":\"" + escapeJson(actionUrl) + "\""
                + "}";

        PushService pushService;
        try {
            pushService = new PushService()
                    .setSubject(vapidSubject)
                    .setPublicKey(Utils.loadPublicKey(vapidPublicKey))
                    .setPrivateKey(Utils.loadPrivateKey(vapidPrivateKey));
        } catch (GeneralSecurityException ex) {
            log.warn("Failed to initialize web push service", ex);
            return;
        }

        for (var subscription : subscriptions) {
            try {
                var notification = new Notification(
                        subscription.getEndpoint(),
                        subscription.getP256dh(),
                        subscription.getAuth(),
                        payloadJson
                );
                pushService.send(notification);
            } catch (Exception ex) {
                log.warn("Failed to send push notification for endpoint {}", subscription.getEndpoint(), ex);
            }
        }
    }

    private boolean isConfigured() {
        return vapidPublicKey != null && !vapidPublicKey.isBlank()
                && vapidPrivateKey != null && !vapidPrivateKey.isBlank();
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}