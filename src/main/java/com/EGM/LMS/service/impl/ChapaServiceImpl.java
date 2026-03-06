package com.EGM.LMS.service.impl;

import com.EGM.LMS.config.ChapaConfig;
import com.EGM.LMS.service.ChapaService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChapaServiceImpl implements ChapaService {

    private static final String INIT_URL = "https://api.chapa.co/v1/transaction/initialize";
    private static final String VERIFY_URL = "https://api.chapa.co/v1/transaction/verify/";

    private final ChapaConfig chapaConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String initializeTransaction(
            BigDecimal amount,
            String currency,
            String email,
            String firstName,
            String lastName,
            String txRef,
            String callbackUrl,
            String returnUrl
    ) {
        if (!chapaConfig.isEnabled()) {
            throw new IllegalStateException("Chapa is not configured. Set CHAPA_SECRET_KEY.");
        }

        Map<String, Object> body = new HashMap<>();
        body.put("amount", amount.setScale(0, java.math.RoundingMode.HALF_UP).toString());
        body.put("currency", currency != null ? currency : "ETB");
        body.put("email", email);
        body.put("first_name", firstName != null ? firstName : "Customer");
        body.put("last_name", lastName != null ? lastName : "User");
        body.put("tx_ref", txRef);
        body.put("callback_url", callbackUrl);
        body.put("return_url", returnUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(chapaConfig.getSecretKey());

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                INIT_URL,
                HttpMethod.POST,
                request,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            log.warn("Chapa initialize failed: {}", response.getStatusCode());
            throw new IllegalStateException("Chapa initialize failed: " + response.getStatusCode());
        }

        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode data = root.path("data");
            String checkoutUrl = data.path("checkout_url").asText(null);
            if (checkoutUrl == null || checkoutUrl.isBlank()) {
                checkoutUrl = root.path("checkout_url").asText(null);
            }
            if (checkoutUrl == null || checkoutUrl.isBlank()) {
                throw new IllegalStateException("Chapa did not return checkout_url");
            }
            return checkoutUrl;
        } catch (Exception e) {
            if (e instanceof IllegalStateException) throw (IllegalStateException) e;
            log.error("Failed to parse Chapa response", e);
            throw new IllegalStateException("Invalid Chapa response", e);
        }
    }

    @Override
    public boolean verifyTransaction(String txRef) {
        if (!chapaConfig.isEnabled()) {
            return false;
        }
        if (txRef == null || txRef.isBlank()) {
            return false;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(chapaConfig.getSecretKey());

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    VERIFY_URL + txRef,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    String.class
            );
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                return false;
            }
            JsonNode root = objectMapper.readTree(response.getBody());
            String status = root.path("data").path("status").asText("");
            return "success".equalsIgnoreCase(status);
        } catch (Exception e) {
            log.warn("Chapa verify failed for tx_ref={}", txRef, e);
            return false;
        }
    }
}
