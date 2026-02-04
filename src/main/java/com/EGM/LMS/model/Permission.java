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
 * Represents a specific functional capability within the LMS.
 * Permissions are assigned to roles to grant fine-grained access control.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "permissions")
@Builder
public class Permission {

    /** * Primary Key
     */
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    /** * Unique identifier for code logic (e.g., "course.edit", "report.view")
     */
    private String name;

    /** * Human-readable label for the UI (e.g., "Edit Course Details")
     */
    private String displayName;

    /** * Groups permissions by functional area (e.g., "COURSES", "FINANCE", "USERS")
     */
    private String module;

    /** * Detailed explanation of what this specific permission allows a user to do
     */
    private String description;

    /** * Audit timestamp for when this permission was added to the system
     */

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // --- Standard Constructors, Getters, and Setters ---
}
