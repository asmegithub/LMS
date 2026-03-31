package com.EGM.LMS.service;

import com.EGM.LMS.dto.CertificateDTO;

import java.util.List;
import java.util.UUID;

public interface CertificateService {
    CertificateDTO createCertificate(CertificateDTO certificate);
    List<CertificateDTO> getMyCertificates();
    List<CertificateDTO> getAllCertificates();
    CertificateDTO getCertificate(UUID certificateId);
    /** Issue a certificate for a completed enrollment (idempotent). */
    CertificateDTO issueForEnrollment(UUID enrollmentId);
    /** Admin: generate a sample certificate for preview/testing. */
    CertificateDTO generateSampleCertificate();
    /** Admin: delete a sample certificate (never deletes real certificates). */
    void deleteSampleCertificate(UUID certificateId);
    CertificateDTO updateCertificate(UUID certificateId, CertificateDTO certificate);
    void deleteCertificate(UUID certificateId);
}
