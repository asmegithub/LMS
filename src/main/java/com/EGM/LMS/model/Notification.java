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
 * Represents a system notification sent to a user.
 * Supports localization and deep-linking to specific app entities.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notifications")
@Builder
public class Notification {

    /** * Primary Key
     */
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    /** * Foreign Key linking to the User receiving the notification
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /** * Categorizes the alert (e.g., "SYSTEM", "COURSE_UPDATE", "PAYMENT")
     */
    private String type; // Mapped from ENUM

    /** * Title in default language (English)
     */
    private String title;

    /** * Title localized for Amharic
     */
    private String titleAm;

    /** * Title localized for Oromiffa
     */
    private String titleOm;
    /** * Title localized for geez
     */
    private String titleGz;

    /** * Main content of the notification in English
     */
    private String message;

    /** * Main content localized for Amharic
     */
    private String messageAm;

    /** * Main content localized for Oromiffa
     */
    private String messageOm;
    /** * Main content localized for geez
     */
    private String messageGz;

    /** * Tracks if the user has seen/opened the alert (TINYINT 1)
     */
    private Boolean isRead;

    /** * The ID of the specific entity related to this alert (e.g., courseId)
     */
    private String relatedId;

    /** * The type of the related entity (e.g., "Course", "Quiz")
     */
    private String relatedType;

    /** * Deep-link URL for the frontend to navigate the user on click
     */
    private String actionUrl;

    /** * Flexible storage for extra data (e.g., icons, sender avatars)
     */
    private String metadata; // Mapped from JSON

    /** * Timestamp of when the notification was generated
     */
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


    // --- Standard Constructors, Getters, and Setters ---
}
