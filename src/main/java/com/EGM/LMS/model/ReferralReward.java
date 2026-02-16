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
 * Represents a reward earned through the platform's referral program.
 * Tracks the incentive amount and whether it has been disbursed to the user.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "referral_rewards")
@Builder
public class ReferralReward {

    /** * Primary Key
     */
    @Id
    @GeneratedValue
    @UuidGenerator
        @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    /** * Foreign Key linking to the specific referral instance
     */
    @ManyToOne
    @JoinColumn(name = "referral_id")
    private Referral referral;

    /** * Foreign Key linking to the User receiving the reward
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /** * The monetary value of the reward
     */
    private BigDecimal amount;

    /** * The category of reward (e.g., "SIGNUP_BONUS", "PURCHASE_COMMISSION")
     */
    private String type;

    /** * Contextual details regarding why the reward was granted
     */
    private String description;

    /** * Flag indicating if the reward has been paid out (TINYINT 1)
     */
    private boolean isPaidOut;

    /** * Timestamp of when the payout was successfully processed
     */
    private LocalDateTime paidOutAt;

    /** * Timestamp of when the reward was initially granted
     */
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // --- Standard Constructors, Getters, and Setters ---
}