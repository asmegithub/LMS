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
 * Represents a Coupon entity mapped from the LMS database schema.
 * Handles discount logic, usage limits, and validity periods.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "coupons")
@Builder
public class Coupon {

    /** * Primary Key
     */
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    /** * The unique code users enter (e.g., "SUMMER2026")
     */
    private String code;

    /** * Detailed explanation of the coupon's purpose
     */
    private String description;

    /** * Discount classification (e.g., PERCENTAGE, FIXED_AMOUNT)
     */
    private String type; // Recommended: Use an Enum

    /** * The numerical value of the discount (e.g., 10.00 for 10%)
     */
    private BigDecimal value;

    /** * The currency the coupon applies to (e.g., "ETB", "USD")
     */
    private String currency;

    /** * Minimum cart value required to use the coupon
     */
    private BigDecimal minPurchase;

    /** * Ceiling for the discount amount (vital for percentage coupons)
     */
    private BigDecimal maxDiscount;

    /** * Maximum total times this coupon can be used across all users
     */
    private int usageLimit;

    /** * Counter for how many times the coupon has been applied
     */
    private int usageCount;

    /** * Maximum times a single user can apply this coupon
     */
    private int perUserLimit;

    /** * Flag to manually enable or disable the promotion
     */
    private boolean isActive;

    /** * The date the coupon becomes valid
     */
    private LocalDateTime startDate;

    /** * The expiration date of the coupon
     */
    private LocalDateTime endDate;

    /** * Timestamp of record creation
     */
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // --- Standard Constructors, Getters, and Setters ---
}
