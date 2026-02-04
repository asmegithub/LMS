package com.EGM.LMS.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a Payout entity mapped from the LMS database schema.
 * Tracks the distribution of earnings to instructors via various payment methods.
 */
@Entity
@Table(name = "payouts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payout {

    /** * Primary Key */
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    /** * Foreign Key linking to the Instructor receiving the funds */
    @ManyToOne
    @JoinColumn(name = "instructor_profile_id")
    private InstructorProfile instructorProfile;

    /** * The exact amount being transferred */
    private BigDecimal amount;

    /** * The currency of the payout (e.g., "ETB", "USD") */
    private String currency;

    /** * Current status (e.g., PENDING, PROCESSING, COMPLETED, FAILED) */
    private String status; // Recommended: Use an Enum

    /** * The method used for transfer (e.g., BANK_TRANSFER, TELEBIRR, CHAPA) */
    private String paymentMethod;

    /** * Detailed information about the destination (e.g., Bank Name, Account Number) */
    private String paymentDetails; // Mapped from JSON

    /** * Unique ID provided by the bank or gateway for tracking */
    private String referenceId;

    /** * Detailed error message if the payout failed */
    private String failureReason;

    /** * Timestamp of when the instructor requested the funds */
    private LocalDateTime requestedAt;

    /** * Timestamp of when the platform successfully processed the payment */
    private LocalDateTime processedAt;

    /** * Audit timestamp for record creation */
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // --- Standard Constructors, Getters, and Setters ---
}
