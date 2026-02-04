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
 * Represents a review and approval workflow for a course.
 * Tracks the transition of a course from "Submitted" to "Approved" or "Rejected".
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "course_approvals")
@Builder
public class CourseApproval {

    /** * Primary Key
     */
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    /** * Foreign Key linking to the Course being reviewed
     */
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    /** * Foreign Key linking to the Admin/User performing the review
     */
    @ManyToOne
    @JoinColumn(name = "reviewer_id")
    private User reviewer;

    /** * Current state of the approval (e.g., PENDING, APPROVED, REJECTED)
     */
    private String status; // Mapped from ENUM

    /** * Timestamp of when the instructor submitted the course for review
     */
    private LocalDateTime submittedAt;

    /** * Timestamp of when the reviewer took action on the submission
     */
    private LocalDateTime reviewedAt;

    /** * Detailed explanation provided if the course is rejected
     */
    private String rejectionReason;

    /** * Private internal notes for administrators regarding this review
     */
    private String notes;

    /** * Audit timestamp for when this approval record was created
     */
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // --- Standard Constructors, Getters, and Setters ---
}
