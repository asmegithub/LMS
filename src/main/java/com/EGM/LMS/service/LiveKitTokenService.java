package com.EGM.LMS.service;

import com.EGM.LMS.config.LiveKitConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Generates LiveKit access tokens (JWTs) following the LiveKit spec.
 * The token is an HS256‑signed JWT whose claims include a "video" grant.
 */
@Service
@RequiredArgsConstructor
public class LiveKitTokenService {

    private final LiveKitConfig liveKitConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Create a LiveKit access token.
     *
     * @param identity    unique participant identity (e.g. user email or id)
     * @param name        display name shown in the room
     * @param roomName    the room to grant access to
     * @param canPublish  whether the participant can publish audio/video (true for instructor)
     * @param canSubscribe whether the participant can subscribe to others' tracks
     * @return signed JWT string
     */
    public String createToken(String identity, String name, String roomName,
                              boolean canPublish, boolean canSubscribe, boolean roomAdmin) {
        try {
            long now = System.currentTimeMillis() / 1000;
            long exp = now + 6 * 3600; // 6 hours

            // --- Header ---
            Map<String, Object> header = new LinkedHashMap<>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");

            // --- Video Grant ---
            Map<String, Object> videoGrant = new LinkedHashMap<>();
            videoGrant.put("room", roomName);
            videoGrant.put("roomJoin", true);
            videoGrant.put("canPublish", canPublish);
            videoGrant.put("canSubscribe", canSubscribe);
            videoGrant.put("canPublishData", true);
            videoGrant.put("roomAdmin", roomAdmin);

            // --- Claims ---
            Map<String, Object> claims = new LinkedHashMap<>();
            claims.put("iss", liveKitConfig.getApiKey());
            claims.put("sub", identity);
            claims.put("name", name);
            claims.put("iat", now);
            claims.put("nbf", now);
            claims.put("exp", exp);
            claims.put("jti", UUID.randomUUID().toString());
            claims.put("video", videoGrant);

            String headerB64 = base64UrlEncode(objectMapper.writeValueAsBytes(header));
            String claimsB64 = base64UrlEncode(objectMapper.writeValueAsBytes(claims));
            String signingInput = headerB64 + "." + claimsB64;

            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(
                    liveKitConfig.getApiSecret().getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"));
            byte[] sig = mac.doFinal(signingInput.getBytes(StandardCharsets.UTF_8));
            String sigB64 = base64UrlEncode(sig);

            return signingInput + "." + sigB64;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate LiveKit token", e);
        }
    }

    private static String base64UrlEncode(byte[] data) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }
}
