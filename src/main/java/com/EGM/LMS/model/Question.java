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
 * Represents a Question entity mapped from the LMS database schema.
 * Stores individual test items with localized content and performance metadata.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "questions")
@Builder
public class Question {

    /** * Primary Key
     */
    @Id
    @GeneratedValue
    @UuidGenerator
        @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    /** * Foreign Key linking to the parent Quiz
     */
    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    /** * Question content in English, Amharic, and Oromo
     */
    private String questionText;
    private String questionTextAm;
    private String questionTextOm;
    private String questionTextGz;


    /** * Question format (e.g., MULTIPLE_CHOICE, TRUE_FALSE, SHORT_ANSWER)
     */
    private String type; // Recommended: Use an Enum

    /** * Educational feedback/explanation provided after the question is answered
     */
    private String explanation;
    private String explanationAm;
    private String explanationOm;
    private String explanationGz;


    /** * Number of points or weight assigned to this specific question
     */
    private int points;

    /** * Order of appearance if shuffle is disabled
     */
    private int orderIndex;

    /** * Optional URL for an image to accompany the question
     */
    private String imageUrl;

    /** * Timestamp of record creation
     */
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // --- Standard Constructors, Getters, and Setters ---
}
