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
public class InstructorBankDetailDTO {

    private UUID id;
    private InstructorProfileDTO instructorProfile;
    private String bankName;
    private String accountName;
    private String accountNumber;
    private String branchName;
    private String swiftCode;
    private Boolean isPrimary;
    private Boolean isVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
