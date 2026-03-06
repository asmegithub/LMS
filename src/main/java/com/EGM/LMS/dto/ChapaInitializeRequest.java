package com.EGM.LMS.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ChapaInitializeRequest {
    private UUID courseId;
    /** Slug for building return URL (e.g. course slug or id string). */
    private String slug;
    /** Optional referrer user ID for referral credit. */
    private UUID referrerId;
}
