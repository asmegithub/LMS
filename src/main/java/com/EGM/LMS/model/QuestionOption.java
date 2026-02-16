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
 * Represents a single choice within a multiple-choice or multi-select question.
 * Supports localization for Amharic and Oromo markets.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "question_options")
@Builder
public class QuestionOption {

    /** * Primary Key */
    @Id
    @GeneratedValue
    @UuidGenerator
        @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    /** * Foreign Key linking back to the parent Question */
    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    /** * Option text in English */
    private String optionText;

    /** * Option text in Amharic */
    private String optionTextAm;

    /** * Option text in Oromo */
    private String optionTextOm;

    /** * Option text in geez */
    private String optionTextGz;

    /** * Flag to identify if this specific option is the right answer */
    private Boolean isCorrect;

    /** * The display sequence of this option (1st, 2nd, 3rd, etc.) */
    private Integer orderIndex;

    /** * Audit timestamp for record creation */
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // --- Standard Constructors, Getters, and Setters ---
}