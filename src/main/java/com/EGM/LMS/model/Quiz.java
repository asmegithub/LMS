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
 * Represents a Quiz entity mapped from the LMS database schema.
 * Manages assessment settings including passing criteria, time limits, and behavior.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "quizzes")
@Builder
public class Quiz {

    /** * Primary Key */
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    /** * Foreign Key linking to the specific Lesson this quiz belongs to */
    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    /** * Quiz titles localized in English, Amharic, and Oromo */
    private String title;
    private String titleAm;
    private String titleOm;

    /** * Detailed instructions or description of the quiz content */
    private String description;

    /** * Type of quiz (e.g., MULTIPLE_CHOICE, TRUE_FALSE) */
    private String quizType; // Recommended: Use an Enum

    /** * Minimum score required to pass (e.g., 70) */
    private int passingScore;

    /** * Maximum number of times a student can retake the quiz */
    private int maxAttempts;

    /** * Duration allowed for the quiz in minutes */
    private int timeLimit;

    /** * Flag to randomize the order of questions for each attempt */
    private boolean shuffleQuestions;

    /** * Flag to randomize the order of answer options within questions */
    private boolean shuffleOptions;

    /** * Controls when/if correct answers are revealed (e.g., ALWAYS, AFTER_PASS) */
    private String showCorrectAnswers; // Recommended: Use an Enum

    /** * Flag to enable or disable the quiz */
    private boolean isActive;

    /** * Timestamp of record creation */
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // --- Standard Constructors, Getters, and Setters ---
}
