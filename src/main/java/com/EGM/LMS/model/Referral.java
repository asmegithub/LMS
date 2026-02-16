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
 * Represents a referral relationship between users.
 * Tracks the conversion status and potential rewards for the growth program.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "referrals")
@Builder
public class Referral {

    /** * Primary Key
     */
    @Id
    @GeneratedValue
    @UuidGenerator
        @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    /** * Foreign Key linking to the User who shared the code
     */
    @ManyToOne
    @JoinColumn(name = "referrer_id")
    private User referrer;

    /** * Foreign Key linking to the new User who joined
     */
    @ManyToOne
    @JoinColumn(name = "referee_id")
    private User referee;

    /** * The specific code used during the signup process
     */
    private String referralCode;

    /** * Current state of the referral (e.g., "PENDING", "SUCCESSFUL", "EXPIRED")
     */
    private String status;

    /** * The expected reward value if conditions are met
     */
    private BigDecimal rewardAmount;

    /** * Flag indicating if the reward has been granted to the referrer (TINYINT 1)
     */
    private Boolean rewardGiven;

    /** * Flag tracking if the new user has made their first purchase (TINYINT 1)
     */
    private Boolean referredUserPurchased;

    /** * Timestamp of when the reward was officially issued
     */
    private LocalDateTime rewardedAt;

    /** * Timestamp of when the referral record was created (Signup date)
     */
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // --- Standard Constructors, Getters, and Setters ---
}
