package com.EGM.LMS.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a Lesson entity mapped from the LMS database schema.
 * Handles diverse content types including adaptive video, documents, and rich text.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lessons")
@Builder
public class Lesson {

    /** * Primary Key */
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    /** * Foreign Key linking to the parent CourseSection */
    @ManyToOne
    @JoinColumn(name = "section_id")
    private CourseSection section;

    /** * Localized titles (English, Amharic, Oromo) */
    private String title;
    private String titleAm;
    private String titleOm;

    /** * Content classification (e.g., VIDEO, DOCUMENT, QUIZ, TEXT) */
    private String type; // Recommended: Use an Enum

    /** * Original source video URL */
    private String videoUrl;

    /** * Transcoded video URLs for adaptive bitrate streaming */
    private String videoUrl240p;
    private String videoUrl360p;
    private String videoUrl720p;

    /** * Secure URL for encrypted video playback */
    private String encryptedVideoUrl;

    /** * Duration of the lesson in seconds */
    private Integer duration;

    /** * External link for downloadable lesson materials */
    private String documentUrl;

    /** * MIME type or extension of the associated document */
    private String documentType;

    /** * Primary text content for the lesson (supports HTML/Markdown) */
    private String content;

    /** * Sorting order within the section */
    private Integer orderIndex;

    /** * Flag indicating if the lesson can be viewed without a subscription */
    private Boolean isFree;

    /** * Flag indicating if students can save the video for offline use */
    private Boolean isDownloadable;

    /** * Visibility flag controlled by the instructor */
    private Boolean isPublished;

    /** * Timestamp of record creation */
    private LocalDateTime createdAt;

    /** * Timestamp of the last content update */
    private LocalDateTime updatedAt;

    // --- Standard Constructors, Getters, and Setters ---
}
