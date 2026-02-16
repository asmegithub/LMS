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
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a template for generating course completion certificates.
 * Stores raw HTML/CSS and design assets for dynamic rendering.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "certificate_templates")
@Builder
public class CertificateTemplate {

    /** * Primary Key
     */
    @Id
    @GeneratedValue
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;


    /** * Friendly name for the template (e.g., "Professional Dark Mode", "Classic Gold")
     */
    private String name;

    /** * Internal description of the template design or intended use
     */
    private String description;

    /** * The raw HTML structure of the certificate
     */
    private String templateHtml; // Mapped from LONGTEXT

    /** * The accompanying CSS styles for the certificate
     */
    private String templateCss; // Mapped from LONGTEXT

    /** * URL for the background graphic (e.g., border, watermark, or seal)
     */
    private String backgroundUrl;

    /** * Flag to mark this as the system-wide default template (TINYINT 1)
     */
    private Boolean isDefault;

    /** * Flag to enable or disable this template from the selection menu
     */
    private Boolean isActive;

    /** * Audit timestamp for when the template was created
     */
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // --- Standard Constructors, Getters, and Setters ---
}
