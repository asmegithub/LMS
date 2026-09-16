package com.EGM.LMS.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Audit log for referral credit: EARNED (friend enrolled), USED (applied to course), WITHDRAWAL.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "referral_credit_transactions")
@Builder
public class ReferralCreditTransaction {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Positive for credit (EARNED), negative for debit (USED, WITHDRAWAL). */
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 32)
    private String type; // EARNED, USED, WITHDRAWAL

    @Column(columnDefinition = "uuid")
    private UUID referenceId; // enrollment id or withdrawal request id

    @CreationTimestamp
    private LocalDateTime createdAt;
}
