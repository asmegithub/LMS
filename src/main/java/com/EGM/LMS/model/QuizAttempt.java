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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a QuizAttempt entity mapped from the LMS database schema.
 * Records the performance and timing details of a student's quiz session.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "quiz_attempts")
@Builder
public class QuizAttempt {

    /** * Primary Key */
    @Id
    @GeneratedValue
    @UuidGenerator
        @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    /** * Foreign Key linking to the Student who took the quiz */
    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    /** * Foreign Key linking to the Quiz being attempted */
    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    /** * The numerical score achieved (e.g., 85.50) */
    private BigDecimal score;

    /** * The raw points earned by the student */
    private int totalPoints;

    /** * The maximum possible points for this quiz version */
    private int maxPoints;

    /** * Flag indicating if the student met the passing criteria */
    private boolean isPassed;

    /** * Tracks if this is the 1st, 2nd, or Nth attempt by the student */
    private int attemptNumber;

    /** * Total duration of the attempt in seconds */
    private int timeTaken;

    /** * When the student started the quiz */
    private LocalDateTime startedAt;

    /** * When the student submitted the quiz */
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // --- Standard Constructors, Getters, and Setters ---
}
