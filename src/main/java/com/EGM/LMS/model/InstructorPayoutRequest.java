package com.EGM.LMS.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "instructor_payout_requests")
@Builder
public class InstructorPayoutRequest {

    @Id
    @GeneratedValue
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_profile_id", nullable = false)
    private InstructorProfile instructorProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_detail_id")
    private InstructorBankDetail bankDetail;

    /** System-wide payout method option selected by instructor. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "method_option_id")
    private PayoutMethodOption methodOption;

    /** JSON string with payout destination details entered by instructor. */
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String payoutDetailsJson;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 32)
    @Builder.Default
    private String status = "PENDING";

    /** Admin rejection reason (visible to instructor). */
    @Column(length = 500)
    private String rejectionReason;

    /** Stored receipt file name uploaded by admin after transfer. */
    @Column(length = 255)
    private String receiptStoredFileName;

    /** Original receipt file name uploaded by admin. */
    @Column(length = 255)
    private String receiptOriginalFileName;

    /** Reviewed by admin. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id")
    private User reviewer;

    private LocalDateTime reviewedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
