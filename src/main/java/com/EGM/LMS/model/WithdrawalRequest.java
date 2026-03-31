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

/**
 * Student request to withdraw referral balance. Status PENDING until admin pays and approves.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "withdrawal_requests")
@Builder
public class WithdrawalRequest {

    @Id
    @GeneratedValue
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Selected payout method (Telebirr, bank, etc.) — same catalog as instructor payouts. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "method_option_id")
    private PayoutMethodOption methodOption;

    /** JSON string with payout destination details entered by the student. */
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String payoutDetailsJson;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 32)
    @Builder.Default
    private String status = "PENDING"; // PENDING, COMPLETED, REJECTED

    @Column(length = 500)
    private String rejectionReason;

    @Column(length = 255)
    private String receiptStoredFileName;

    @Column(length = 255)
    private String receiptOriginalFileName;

    @Column(length = 1000)
    private String receiptIssueMessage;

    private LocalDateTime receiptIssueReportedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id")
    private User reviewer;

    private LocalDateTime reviewedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
