package com.EGM.LMS.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

    private UUID id;

    /** * Unique email address for authentication
     */
    private String email;

    /** * Contact phone number
     */
    private String phone;

    /** * Encrypted password string
     */
//    private String passwordHash;

    /** * User's legal first name
     */
    private String firstName;

    /** * User's legal last name
     */
    private String lastName;

    /** * Access level (e.g., STUDENT, INSTRUCTOR, ADMIN)
     */
    private String role; // Map to Enum for type safety

    /** * Flag indicating if the user has verified their identity
     */
    private Boolean isVerified;

    /** * Flag indicating if the account is currently enabled
     */
    private Boolean isActive;

    /** * URL or path to the user's avatar image
     */
    private String profileImage;

    /** * Preferred interface language (e.g., "en", "am", "om")
     */
    private String language;

    /** * Unique code for the user to invite others
     */
    private String referralCode;

    /** * The referralCode of the user who invited this user
     */
    private String referredBy;

    /** * Short personal biography
     */
    private String bio;

    /** * User's regional timezone (e.g., "Africa/Addis_Ababa")
     */
    private String timezone;

    /** * Timestamp of the most recent successful login
     */
    private LocalDateTime lastLoginAt;

    /** * Timestamp of when the email was confirmed
     */
    private LocalDateTime emailVerifiedAt;

    /** * Timestamp of when the phone number was confirmed
     */
    private LocalDateTime phoneVerifiedAt;

    /** * Timestamp of record creation
     */
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
