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
 * Represents an EmailLog entity mapped from the LMS database schema.
 * Tracks the status and details of all email communications sent to users.
 */
@Entity
@Table(name = "email_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailLog {

    /** * Primary Key
     */
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    /** * Foreign Key linking to the User receiving the email
     */
    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private User recipient;

    /** * The recipient's email address at the time of sending
     */
    private String email;

    /** * The subject line of the sent email
     */
    private String subject;

    /** * The type of email (e.g., WELCOME_EMAIL, ENROLLMENT_CONFIRMATION, PASSWORD_RESET)
     */
    private String type; // Recommended: Use an Enum

    /** * The current delivery status (e.g., PENDING, SENT, FAILED, DELIVERED)
     */
    private String status; // Recommended: Use an Enum

    /** * Detailed error message or bounce reason if the status is FAILED
     */
    private String errorMessage;

    /** * Timestamp of when the email was added to the queue
     */
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // --- Standard Constructors, Getters, and Setters ---
}
