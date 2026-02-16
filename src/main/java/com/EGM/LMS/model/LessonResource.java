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
 * Represents a LessonResource entity mapped from the LMS database schema.
 * Manages downloadable supplementary materials for specific lessons.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "lesson_resources")
public class LessonResource {

    /** * Primary Key
     */
    @Id
    @GeneratedValue
    @UuidGenerator
        @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    /** * Foreign Key linking to the parent Lesson
     */
    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    /** * Resource titles in English, Amharic, and Oromo
     */
    private String title;
    private String titleAm;
    private String titleOm;
    private String titleGz;


    /** * File category (e.g., "PDF", "ZIP", "DOCX")
     */
    private String type;

    /** * Direct download URL or file path
     */
    private String url;

    /** * Size of the file in bytes
     */
    private int fileSize;

    /** * Sorting order for resources within the lesson view
     */
    private int orderIndex;

    /** * Timestamp of record creation
     */
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // --- Standard Constructors, Getters, and Setters ---
}
