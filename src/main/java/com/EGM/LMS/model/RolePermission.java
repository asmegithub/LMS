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
 * Represents the mapping between a Role and a Permission.
 * This determines which specific actions a given role is authorized to perform.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "role_permissions")
@Builder
public class RolePermission {

    /** * Primary Key (Often a composite of roleId and permissionId)
     */
    @Id
    @GeneratedValue
    @UuidGenerator
        @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    /** * Foreign Key linking to the Role
     */
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    /** * Foreign Key linking to the specific Permission
     */
    @ManyToOne
    @JoinColumn(name = "permission_id")
    private Permission permission;

    /** * Audit timestamp for when this permission was assigned to the role
     */
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // --- Standard Constructors, Getters, and Setters ---
}
