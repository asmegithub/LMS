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

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * A system-owned payment receiving account (bank, wallet, etc.) shown to students for manual payments.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payment_accounts")
@Builder
public class PaymentAccount {

    @Id
    @GeneratedValue
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    /** e.g. "Commercial Bank of Ethiopia", "Telebirr", "Awash Bank" */
    private String providerName;

    /** BANK, WALLET, USSD, OTHER */
    @Builder.Default
    private String type = "BANK";

    /** Account holder name (optional for wallets) */
    private String accountName;

    /** Bank account number / wallet number (optional if qrCodeData is used) */
    private String accountNumber;

    /** Optional USSD code or short code (e.g. *847#) */
    private String ussdCode;

    /** Optional QR / deep-link / instructions */
    @Column(columnDefinition = "TEXT")
    private String instructions;

    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

