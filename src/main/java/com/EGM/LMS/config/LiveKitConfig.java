package com.EGM.LMS.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LiveKitConfig {

    @Value("${app.livekit.api-key:devkey}")
    private String apiKey;

    @Value("${app.livekit.api-secret:secret_that_is_at_least_32_bytes_long}")
    private String apiSecret;

    @Value("${app.livekit.url:wss://localhost:7880}")
    private String url;

    public String getApiKey() {
        return apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public String getUrl() {
        return url;
    }
}
