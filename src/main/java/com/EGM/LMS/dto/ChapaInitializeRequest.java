package com.EGM.LMS.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ChapaInitializeRequest {
    /** Single course (legacy). Use courseIds for cart/multi-course. */
    private UUID courseId;
    /** Multiple courses (cart checkout). When set, order + order items are created. */
    private List<UUID> courseIds;
    /** Slug for building return URL (e.g. course slug or id string). */
    private String slug;
    /** Optional referrer user ID for referral credit. */
    private UUID referrerId;
}
