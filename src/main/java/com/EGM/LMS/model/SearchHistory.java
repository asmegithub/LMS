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
 * Represents a record of a user's search activity within the LMS.
 * Used for personalization, search analytics, and "recent search" features.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "search_histories")
@Builder
public class SearchHistory {

    /** * Primary Key
     */
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;


    /** * Foreign Key linking to the User who performed the search
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /** * The exact text string entered by the user
     */
    private String query;

    /** * Categorizes the search (e.g., "COURSES", "INSTRUCTORS", "BLOGS")
     */
    private String category;

    /** * Stores the number of results returned for this specific query
     */
    private int resultsCount;

    /** * Flag to allow users to "clear" their history without deleting records (TINYINT 1)
     */
    private boolean isVisible;

    /** * The source of the search (e.g., "WEB_APP", "MOBILE_APP")
     */
    private String source;

    /** * Timestamp of when the search was performed
     */
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // --- Standard Constructors, Getters, and Setters ---
}
