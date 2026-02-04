package com.EGM.LMS.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents the banking information for an instructor.
 * Used by the Payout system to distribute earnings.
 */
@Entity
@Table(name = "instructor_bank_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstructorBankDetail {

    /** * Primary Key
     */
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    /** * Foreign Key linking to the Instructor Profile
     */

    @ManyToOne
    @JoinColumn(name = "instructor_profile_id")
    private InstructorProfile instructorProfile;

    /** * Name of the financial institution (e.g., Commercial Bank of Ethiopia)
     */
    private String bankName;

    /** * Full name of the account holder
     */
    private String accountName;

    /** * Unique account number
     */
    private String accountNumber;

    /** * Specific bank branch name or location
     */
    private String branchName;

    /** * International standard code for bank identification
     */
    private String swiftCode;

    /** * Flag to mark the primary account for automatic payouts (TINYINT 1)
     */
    private Boolean isPrimary;

    /** * Flag indicating if the bank account has been confirmed by admin
     */
    private Boolean isVerified;

    /** * Timestamp of record creation
     */
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // --- Standard Constructors, Getters, and Setters ---
}
