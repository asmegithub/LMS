package com.EGM.LMS.security;

import com.EGM.LMS.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtEncoder jwtEncoder;

    @Value("${app.jwt.access-token-ttl-minutes:15}")
    private long accessTokenTtlMinutes;

    @Value("${app.jwt.refresh-token-ttl-hours:720}")
    private long refreshTokenTtlHours;

    public String generateAccessToken(User user) {
        var now = Instant.now();
        var expiresAt = now.plus(Duration.ofMinutes(accessTokenTtlMinutes));
        var claims = JwtClaimsSet.builder()
                .issuer("LMS")
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(user.getEmail())
                .claim("role", user.getRole())
                .claim("userId", user.getId() != null ? user.getId().toString() : null)
                .build();

        var header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public long getAccessTokenTtlSeconds() {
        return Duration.ofMinutes(accessTokenTtlMinutes).getSeconds();
    }

    public Duration getRefreshTokenTtl() {
        return Duration.ofHours(refreshTokenTtlHours);
    }
}
