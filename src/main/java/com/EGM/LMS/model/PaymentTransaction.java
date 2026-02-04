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


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payment_transactions")
@Builder

    /**
     * Represents a PaymentTransaction entity mapped from the LMS database schema.
     * Stores granular data for financial auditing and payment gateway integration.
     */
public class PaymentTransaction {

    /** * Primary Key
     */
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    /** * Foreign Key linking to the master Payment record
     */
    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    /** * The payment provider used (e.g., CHAPA, TELEBIRR, STRIPE)
     */
    private String gateway; // Recommended: Use an Enum

    /** * Nature of the transaction (e.g., DEBIT, CREDIT, REFUND)
     */
    private String transactionType;

    /** * The exact amount processed
     */
    private BigDecimal amount;

    /** * The currency used for this specific transaction (e.g., "ETB")
     */
    private String currency;

    /** * The current state of the transaction (e.g., PENDING, SUCCESS, FAILED)
     */
    private String status; // Recommended: Use an Enum

    /** * Unique reference ID returned by the payment gateway
     */
    private String gatewayRef;

    /** * Raw JSON response from the gateway for debugging/logging
     */
    private String gatewayResponse; // Mapped from JSON type

    /** * The IP address of the user at the time of purchase (for fraud detection)
     */
    private String ipAddress;

    /** * Browser/Device information of the user
     */
    private String userAgent;

    /** * Timestamp of when the transaction occurred
     */
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // --- Standard Constructors, Getters, and Setters ---
}
