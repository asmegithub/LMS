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
 * Represents a Download entity mapped from the LMS database schema.
 * Tracks offline content metadata, including file sizes and expiration dates.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "downloads")
@Builder
public class Download {

    /** * Primary Key
     */
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    /** * Foreign Key linking to the User who performed the download
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /** * Foreign Key linking to the specific Lesson downloaded
     */
    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    /** * The quality level of the downloaded video (e.g., "720p", "360p")
     */
    private String videoQuality;

    /** * Local or remote path to the downloaded file
     */
    private String fileUrl;

    /** * Total size of the file in bytes
     */
    private Long fileSize;

    /** * Timestamp when the offline access to this file should be revoked
     */
    private LocalDateTime expiresAt;

    /** * Timestamp of when the actual file download was completed
     */
    private LocalDateTime downloadedAt;

    /** * Timestamp of record creation
     */
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // --- Standard Constructors, Getters, and Setters ---
}