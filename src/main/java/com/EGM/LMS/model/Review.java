package com.EGM.LMS.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    private String studentId;

    /** * Numerical rating (e.g., 1 to 5)
     */
    private Integer rating;

    /** * Short headline or title of the review
     */
    private String title;

    /** * Detailed feedback or review body text
     */
    private String content;

    /** * Flag to control if the review is displayed publicly
     */
    private Boolean isVisible;

    /** * Counter for how many users found this review useful
     */
    private int helpfulCount;

    /** * Timestamp of record creation
     */
    private LocalDateTime createdAt;

    /** * Timestamp of the last update to the review
     */
    private LocalDateTime updatedAt;

    // --- Standard Constructors, Getters, and Setters ---
}