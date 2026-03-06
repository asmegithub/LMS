package com.EGM.LMS.repository;

import com.EGM.LMS.model.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CertificateRepository extends JpaRepository<Certificate, UUID> {

    List<Certificate> findByStudent_IdOrderByIssuedAtDesc(UUID studentId);
}
