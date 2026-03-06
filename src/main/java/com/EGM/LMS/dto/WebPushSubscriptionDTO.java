package com.EGM.LMS.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebPushSubscriptionDTO {
    private String endpoint;
    private Keys keys;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Keys {
        private String p256dh;
        private String auth;
    }
}