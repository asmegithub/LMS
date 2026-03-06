package com.EGM.LMS.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChapaConfig {

    @Value("${app.chapa.secret-key:}")
    private String secretKey;

    @Value("${app.chapa.frontend-base-url:http://localhost:8081}")
    private String frontendBaseUrl;

    @Value("${app.chapa.callback-base-url:http://localhost:8080}")
    private String callbackBaseUrl;

    public String getSecretKey() {
        return secretKey;
    }

    public String getFrontendBaseUrl() {
        return frontendBaseUrl;
    }

    public String getCallbackBaseUrl() {
        return callbackBaseUrl;
    }

    public boolean isEnabled() {
        return secretKey != null && !secretKey.isBlank();
    }
}
