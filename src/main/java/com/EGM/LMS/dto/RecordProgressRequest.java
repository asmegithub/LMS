package com.EGM.LMS.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordProgressRequest {
    private UUID enrollmentId;
    private UUID lessonId;
    private String status; // NOT_STARTED, IN_PROGRESS, COMPLETED
}
