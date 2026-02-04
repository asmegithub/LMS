package com.EGM.LMS.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchHistoryDTO {

    private UUID id;
    private UserDTO user;
    private String query;
    private String category;
    private int resultsCount;
    private boolean isVisible;
    private String source;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
