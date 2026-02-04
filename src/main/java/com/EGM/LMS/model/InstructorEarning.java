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
 * Represents the financial record for an instructor.
 * Tracks total lifetime earnings, current balance, and historical performance.
 */
@Entity
@Table(name = "instructor_earnings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstructorEarning {

    /** * Primary Key
     */
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    /** * Foreign Key linking to the specific Instructor Profile
     */
    @ManyToOne
    @JoinColumn(name = "instructor_profile_id")
    private InstructorProfile instructorProfile;

    /** * Total gross revenue earned before any platform deductions
     */
    private BigDecimal totalEarnings;

    /** * Total amount the instructor has successfully withdrawn to date
     */
    private BigDecimal totalWithdrawn;

    /** * Current balance available for the instructor to withdraw
     */
    private BigDecimal currentBalance;

    /** * Earnings from the most recent completed month
     */
    private BigDecimal lastMonthEarning;

    /** * The date the last withdrawal was processed
     */
    private LocalDateTime lastWithdrawnAt;

    /** * Timestamp of when the financial record was first created
     */
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // --- Standard Constructors, Getters, and Setters ---
}