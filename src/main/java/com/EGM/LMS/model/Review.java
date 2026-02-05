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
 * Represents a Review entity mapped from the LMS database schema.
 * Stores ratings and feedback provided by students for specific courses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reviews")
@Builder
public class Review {

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

    /** * Foreign Key linking to the User (Student) who wrote the review
     */
    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    /** * Numerical rating (e.g., 1 to 5)
     */
    private int rating;

    /** * Short headline or title of the review
     */
    private String title;

    /** * Detailed feedback or review body text
     */
    private String content;

    /** * Flag to control if the review is displayed publicly
     */
    private boolean visible;

    /** * Counter for how many users found this review useful
     */
    private int helpfulCount;

    /** * Timestamp of record creation
     */
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // --- Standard Constructors, Getters, and Setters ---
}