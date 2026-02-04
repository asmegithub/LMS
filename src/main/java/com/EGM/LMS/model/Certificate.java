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
 * Represents an issued course completion certificate.
 * Connects the student's achievement to a specific design template and verification record.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "certificates")
@Builder
public class Certificate {

    /** * Primary Key
     */
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    /** * Foreign Key linking to the specific course enrollment
     */
    @ManyToOne
    @JoinColumn(name = "enrollment_id")
    private Enrollment enrollment;

    /** * Foreign Key linking to the Student (User)
     */
    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    /** * Foreign Key linking to the Course completed
     */
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    /** * Foreign Key linking to the design template used (from certificate_templates)
     */
    @ManyToOne
    @JoinColumn(name = "template_id")
    private CertificateTemplate template;

    /** * Human-readable unique serial number for the certificate
     */
    private String certificateNumber;

    /** * URL to the hosted PDF or image file of the certificate
     */
    private String certificateUrl;

    /** * Unique code for public verification (e.g., used on a /verify-certificate page)
     */
    private String verificationCode;

    /** * Timestamp of when the certificate was officially generated
     */
    private LocalDateTime issuedAt;

    /** * Optional expiration date for certifications that require renewal
     */
    private LocalDateTime expiresAt;

    /** * Audit timestamp for record creation
     */
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


    // --- Standard Constructors, Getters, and Setters ---
}
