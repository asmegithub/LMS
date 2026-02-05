package com.EGM.LMS.service;

import com.EGM.LMS.dto.CertificateTemplateDTO;

import java.util.List;
import java.util.UUID;

public interface CertificateTemplateService {
    CertificateTemplateDTO createCertificateTemplate(CertificateTemplateDTO certificateTemplate);
    List<CertificateTemplateDTO> getAllCertificateTemplates();
    CertificateTemplateDTO getCertificateTemplate(UUID certificateTemplateId);
    CertificateTemplateDTO updateCertificateTemplate(UUID certificateTemplateId, CertificateTemplateDTO certificateTemplate);
    void deleteCertificateTemplate(UUID certificateTemplateId);
}
