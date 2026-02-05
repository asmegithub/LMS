package com.EGM.LMS.service;

import com.EGM.LMS.dto.CertificateDTO;

import java.util.List;
import java.util.UUID;

public interface CertificateService {
    CertificateDTO createCertificate(CertificateDTO certificate);
    List<CertificateDTO> getAllCertificates();
    CertificateDTO getCertificate(UUID certificateId);
    CertificateDTO updateCertificate(UUID certificateId, CertificateDTO certificate);
    void deleteCertificate(UUID certificateId);
}
