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
 * Represents a CourseSection entity mapped from the LMS database schema.
 * Sections act as containers to group related lessons within a course.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "course_sections")
@Builder
public class CourseSection {

    /** * Primary Key
     */
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    /** * Foreign Key linking to the parent Course
     */
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    /** * Standard section title
     */
    private String title;

    /** * Section title in Amharic
     */
    private String titleAm;

    /** * Section title in Oromo
     */
    private String titleOm;

    /** * Optional detailed description of what this section covers
     */
    private String description;

    /** * The position of the section within the course (for UI sorting)
     */
    private int orderIndex;

    /** * Timestamp of record creation
     */
    private LocalDateTime createdAt;

    /** * Timestamp of the last update to the section
     */
    private LocalDateTime updatedAt;

    // --- Standard Constructors, Getters, and Setters ---
}
