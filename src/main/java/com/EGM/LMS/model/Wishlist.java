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
 * Represents a Wishlist entity mapped from the LMS database schema.
 * Tracks courses that a user has saved for future interest or purchase.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "wishlists")
@Builder
public class Wishlist {

    /** * Primary Key
     */
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    /** * Foreign Key linking to the User who saved the course
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /** * Foreign Key linking to the Course being saved
     */
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    /** * Timestamp of when the course was added to the wishlist
     */
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // --- Standard Constructors, Getters, and Setters ---
}