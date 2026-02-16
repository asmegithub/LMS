package com.EGM.LMS.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a global configuration entry for the LMS.
 * Allows for dynamic adjustments of system behavior and site-wide constants.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "system_settings")
@Builder
public class SystemSetting {

    /** * Primary Key
     */
    @Id
    @GeneratedValue
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    /** * Unique identifier for the setting (e.g., "SITE_NAME", "REFUND_PERIOD_DAYS")
     */
    @Column(name = "setting_key")
    private String key;

    /** * The value of the setting, stored as JSON to support strings, numbers, or arrays
     */
    @Column(name = "setting_value")
    private String value; // Mapped from JSON

    /** * Explains what this setting controls for other administrators
     */
    private String description;

    /** * Flag to determine if this setting can be exposed to the public API (TINYINT 1)
     */
    private Boolean isPublic;

    /** * Foreign Key linking to the Administrator who last changed this value
     */
    private String updatedBy;

    /** * Audit timestamp for when the setting was first added
     */
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // --- Standard Constructors, Getters, and Setters ---
}
