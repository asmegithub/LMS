package com.EGM.LMS.repository;

import com.EGM.LMS.model.CertificateTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CertificateTemplateRepository extends JpaRepository<CertificateTemplate, UUID> {
}
