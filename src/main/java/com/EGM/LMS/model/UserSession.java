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
 * Represents a secure user session for authentication.
 * Manages access tokens, device tracking, and session expiration.
 */
@Entity
@Table(name = "user_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSession {

    /** * Primary Key
     */
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;


    /** * Foreign Key linking to the User
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /** * The short-lived JWT or access token
     */
    private String token;

    /** * Long-lived token used to generate new access tokens
     */
    private String refreshToken;

    /** * Categorizes the device (e.g., "Mobile", "Web", "Tablet")
     */
    private String deviceType;

    /** * Specific name of the device (e.g., "iPhone 15", "Chrome on Windows")
     */
    private String deviceName;

    /** * The last known IP address of the user
     */
    private String ipAddress;

    /** * Raw browser/app information for security auditing
     */
    private String userAgent;

    /** * Flag to manually revoke a session (TINYINT 1)
     */
    private boolean isActive;

    /** * Timestamp when the session will no longer be valid
     */
    private LocalDateTime expiresAt;

    /** * Tracks the user's "Last Seen" time for active monitoring
     */
    private LocalDateTime lastActiveAt;

    /** * Audit timestamp for when the login occurred
     */
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // --- Standard Constructors, Getters, and Setters ---
}