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
 * Represents a Payment entity mapped from the LMS database schema.
 * Handles transaction tracking, revenue splits, and discount applications.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payments")
@Builder
public class Payment {

    /** * Primary Key */
    @Id
    @GeneratedValue
    @UuidGenerator
        @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    /** * Unique identifier from the payment gateway (e.g., Stripe/PayPal) */
    private String transactionId;

    /** * Foreign Key linking to the User (Student) */
    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    /** * Foreign Key linking to the Order (multi-course checkout). When set, course may be null. */
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    /** * Foreign Key linking to the Course purchased (single-course flow). Null when order is set. */
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    /** * Total amount paid by the customer */
    private BigDecimal amount;

    /** * Currency code (e.g., "USD", "ETB") */
    private String currency;

    /** * The provider used (e.g., Chapa, Stripe, PayPal) */
    private String gateway; // Recommended: use an Enum

    /** * Transaction status (e.g., COMPLETED, PENDING, REFUNDED) */
    private String status; // Recommended: use an Enum

    /** * Amount after gateway fees are deducted */
    private BigDecimal netAmount;

    /** * The portion of revenue kept by the LMS platform */
    private BigDecimal platformShare;

    /** * The portion of revenue allocated to the instructor */
    private BigDecimal instructorShare;

    /** * Foreign Key linking to a used Coupon (if any) */
    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    /** * Total discount value subtracted from the original price */
    private BigDecimal discountAmount;

    /** * Code used if the purchase was via a referral link */
    private String referralCode;

    /** * Discount applied specifically due to a referral */
    private BigDecimal referralDiscount;

    /** * Raw JSON response string from the payment gateway for auditing */
    private String gatewayResponse;

    /** * Internal reference number from the gateway provider */
    private String gatewayReference;

    /** * Timestamp of when the payment was successfully processed */
    private LocalDateTime paidAt;

    /** * Timestamp of record creation */

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // --- Standard Constructors, Getters, and Setters ---
}
