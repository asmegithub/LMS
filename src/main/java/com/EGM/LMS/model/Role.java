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
 * Represents a user role within the system's security architecture.
 * Used to manage permissions and access levels for different user types.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
@Builder
public class Role {

    /** * Primary Key
     */
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    /** * Internal slug/name used in code logic (e.g., "ROLE_ADMIN")
     */
    private String name;

    /** * Friendly name shown in the UI (e.g., "System Administrator")
     */
    private String displayName;

    /** * Detailed explanation of the responsibilities tied to this role
     */
    private String description;

    /** * Flag to prevent deletion of core system roles (TINYINT 1)
     */
    private Boolean isSystem;

    /** * Audit timestamp for when the role was defined
     */
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // --- Standard Constructors, Getters, and Setters ---
}