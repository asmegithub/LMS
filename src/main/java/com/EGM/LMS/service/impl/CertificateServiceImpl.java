package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.CertificateDTO;
import com.EGM.LMS.dto.CertificateTemplateDTO;
import com.EGM.LMS.dto.CourseDTO;
import com.EGM.LMS.dto.EnrollmentDTO;
import com.EGM.LMS.dto.UserDTO;
import com.EGM.LMS.model.Certificate;
import com.EGM.LMS.repository.CertificateRepository;
import com.EGM.LMS.repository.CertificateTemplateRepository;
import com.EGM.LMS.repository.CourseRepository;
import com.EGM.LMS.repository.EnrollmentRepository;
import com.EGM.LMS.repository.UserRepository;
import com.EGM.LMS.service.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CertificateServiceImpl implements CertificateService {
    private final CertificateRepository certificateRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final CertificateTemplateRepository certificateTemplateRepository;

    @Override
    public CertificateDTO createCertificate(CertificateDTO certificate) {
        return toDto(certificateRepository.save(toEntity(certificate)));
    }

    @Override
    public List<CertificateDTO> getMyCertificates() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            throw new IllegalStateException("Authentication required");
        }
        var currentUser = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        List<Certificate> certificates = certificateRepository.findByStudent_IdOrderByIssuedAtDesc(currentUser.getId());
        return certificates.stream().map(this::toDto).toList();
    }

    @Override
    public List<CertificateDTO> getAllCertificates() {
        var certificates = certificateRepository.findAll();
        var certificateDtos = new java.util.ArrayList<CertificateDTO>();
        for (Certificate certificate : certificates) {
            certificateDtos.add(toDto(certificate));
        }
        return certificateDtos;
    }

    @Override
    public CertificateDTO getCertificate(UUID certificateId) {
        return toDto(certificateRepository.findById(certificateId).orElseThrow());
    }

    @Override
    public CertificateDTO updateCertificate(UUID certificateId, CertificateDTO certificate) {
        certificateRepository.findById(certificateId).orElseThrow();
        var entity = toEntity(certificate);
        entity.setId(certificateId);
        return toDto(certificateRepository.save(entity));
    }

    @Override
    public void deleteCertificate(UUID certificateId) {
        certificateRepository.deleteById(certificateId);
    }

    private Certificate toEntity(CertificateDTO certificate) {
        var enrollmentId = certificate.getEnrollment() != null ? certificate.getEnrollment().getId() : null;
        var studentId = certificate.getStudent() != null ? certificate.getStudent().getId() : null;
        var courseId = certificate.getCourse() != null ? certificate.getCourse().getId() : null;
        var templateId = certificate.getTemplate() != null ? certificate.getTemplate().getId() : null;
        return Certificate.builder()
                .enrollment(enrollmentId != null ? enrollmentRepository.findById(enrollmentId).orElse(null) : null)
                .student(studentId != null ? userRepository.findById(studentId).orElse(null) : null)
                .course(courseId != null ? courseRepository.findById(courseId).orElse(null) : null)
                .template(templateId != null ? certificateTemplateRepository.findById(templateId).orElse(null) : null)
                .certificateNumber(certificate.getCertificateNumber())
                .certificateUrl(certificate.getCertificateUrl())
                .verificationCode(certificate.getVerificationCode())
                .issuedAt(certificate.getIssuedAt())
                .expiresAt(certificate.getExpiresAt())
                .build();
    }

    private CertificateDTO toDto(Certificate certificate) {
        return CertificateDTO.builder()
                .id(certificate.getId())
                .enrollment(certificate.getEnrollment() != null ? EnrollmentDTO.builder().id(certificate.getEnrollment().getId()).build() : null)
                .student(certificate.getStudent() != null ? UserDTO.builder().id(certificate.getStudent().getId()).build() : null)
                .course(certificate.getCourse() != null ? CourseDTO.builder().id(certificate.getCourse().getId()).build() : null)
                .template(certificate.getTemplate() != null ? CertificateTemplateDTO.builder().id(certificate.getTemplate().getId()).build() : null)
                .certificateNumber(certificate.getCertificateNumber())
                .certificateUrl(certificate.getCertificateUrl())
                .verificationCode(certificate.getVerificationCode())
                .issuedAt(certificate.getIssuedAt())
                .expiresAt(certificate.getExpiresAt())
                .createdAt(certificate.getCreatedAt())
                .updatedAt(certificate.getUpdatedAt())
                .build();
    }
}
