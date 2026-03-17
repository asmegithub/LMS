package com.EGM.LMS.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Manual payment receipt uploaded by a student. Admin must approve to enroll.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payment_proofs")
@Builder
public class PaymentProof {

    @Id
    @GeneratedValue
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    /** Optional: single course (if manual pay from course checkout). */
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    /** Optional: multi-course order (if manual pay from cart). */
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "payment_account_id", nullable = false)
    private PaymentAccount paymentAccount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Builder.Default
    private String currency = "ETB";

    /** PENDING, APPROVED, REJECTED */
    @Builder.Default
    private String status = "PENDING";

    /** Stored filename under uploads/payment-proofs/ */
    @Column(nullable = false)
    private String storedFileName;

    /** Original filename */
    private String originalFileName;

    /** Optional note from student (e.g., sender name, transfer id) */
    @Column(columnDefinition = "TEXT")
    private String note;

    /** Admin reviewer */
    @ManyToOne
    @JoinColumn(name = "reviewer_id")
    private User reviewer;

    private LocalDateTime reviewedAt;

    @Column(columnDefinition = "TEXT")
    private String rejectionReason;

    /** Payment created after approval (so we can enroll using existing logic). */
    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

