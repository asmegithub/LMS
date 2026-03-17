package com.EGM.LMS.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payout_method_options")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayoutMethodOption {

    @Id
    @GeneratedValue
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    /** Display name e.g. "Bank transfer", "Telebirr wallet" */
    @Column(nullable = false, length = 120)
    private String name;

    /** Type identifier e.g. BANK_TRANSFER, TELEBIRR, OTHER */
    @Column(nullable = false, length = 64)
    private String type;

    /** JSON describing required fields for this method (schema-like). */
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String fieldsJson;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

