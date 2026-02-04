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
 * Represents an entry in the system audit log.
 * Records administrative actions, data changes, and access details for security oversight.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "audit_logs")
@Builder
public class AuditLog {

    /** * Primary Key
     */
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;


    /** * Foreign Key linking to the Administrator who performed the action
     */
    @ManyToOne
    @JoinColumn(name = "admin_id")
    private User admin;

    /** * The specific operation performed (e.g., "UPDATE_COURSE", "DELETE_USER", "APPROVE_PAYOUT")
     */
    private String action;

    /** * The type of entity being affected (e.g., "Course", "InstructorProfile", "Payment")
     */
    private String targetType;

    /** * The unique identifier of the specific record being modified
     */
    private String targetId;

    /** * Snapshot of the record before the change, stored as JSON
     */
    private String oldValue;

    /** * Snapshot of the record after the change, stored as JSON
     */
    private String newValue;

    /** * A summary of specifically what changed (e.g., {"price": [50, 40]})
     */
    private String changes; // Mapped from JSON, stringed JSON

    /** * The IP address of the administrator during the action
     */
    private String ipAddress;

    /** * The browser or tool used to perform the action
     */
    private String userAgent;

    /** * Timestamp of when the event occurred
     */
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // --- Standard Constructors, Getters, and Setters ---
}