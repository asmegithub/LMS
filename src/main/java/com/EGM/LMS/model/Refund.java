package com.EGM.LMS.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a Refund entity mapped from the LMS database schema.
 * Tracks the reversal of payments, including status, gateway references, and processing metadata.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "refunds")
@Builder
public class Refund {

    /** * Primary Key
     */
    @Id
    @GeneratedValue
    @UuidGenerator
        @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    /** * Foreign Key linking to the original Payment
     */
    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    /** * The exact amount being refunded
     */
    private BigDecimal amount;

    /** * The official reason for the refund (e.g., "Course Unsatisfactory", "Accidental Purchase")
     */
    private String reason;

    /** * The current lifecycle stage (e.g., PENDING, PROCESSED, FAILED)
     */
    private String status; // Recommended: Use an Enum

    /** * Foreign Key linking to the Admin who authorized the refund
     */
    private String processedBy;

    /** * Timestamp of when the refund was finalized by the gateway
     */
    private LocalDateTime processedAt;

    /** * The unique identifier provided by the payment gateway (e.g., Chapa, Telebirr)
     */
    private String gatewayRef;

    /** * Internal administrative notes regarding the refund process
     */
    private String notes;

    /** * Audit timestamp for record creation
     */
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // --- Standard Constructors, Getters, and Setters ---
}