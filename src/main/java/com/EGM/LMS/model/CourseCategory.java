package com.EGM.LMS.model;

import java.time.LocalDateTime;

import java.util.UUID;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

//import com.EGM.LMS.model.CourseCategory;
/**
 * Represents a CourseCategory entity mapped from the LMS database schema.
 * This class supports multi-language names and hierarchical category structures.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "course_categories")
@Builder
public class CourseCategory {

    /** * Primary Key
     */
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    /** * Standard category name
     */
    private String name;

    /** * Localized name in Amharic
     */
    private String nameAm;

    /** * Localized name in Oromo
     */
    private String nameOm;

    /** * URL-friendly identifier
     */
    private String slug;

    /** * Detailed description of the category
     */
    private String description;

    /** * Icon identifier or URL for UI display
     */
    private String icon;

    /** * Foreign Key for parent category (Self-referencing relationship)
     */
    private String parentId;

    /** * Sorting order for display in lists or menus
     */
    private int orderIndex;

    /** * Status flag to enable/disable the category
     */
    private Boolean isActive;

    /** * Timestamp of record creation
     */
    private LocalDateTime createdAt;

    /** * Timestamp of the last update
     */
    private LocalDateTime updatedAt;

    // --- Constructors, Getters, and Setters ---
}