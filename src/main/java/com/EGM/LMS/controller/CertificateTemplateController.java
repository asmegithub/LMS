package com.EGM.LMS.controller;

import com.EGM.LMS.dto.CertificateTemplateDTO;
import com.EGM.LMS.service.CertificateTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/certificate-templates")
@PreAuthorize("hasAuthority('certificate-templates.manage')")
public class CertificateTemplateController {
    private final CertificateTemplateService certificateTemplateService;

    @PostMapping
    ResponseEntity<CertificateTemplateDTO> createCertificateTemplate(
            @RequestBody CertificateTemplateDTO certificateTemplateDto) {
        return ResponseEntity.ok(certificateTemplateService.createCertificateTemplate(certificateTemplateDto));
    }

    @GetMapping
    ResponseEntity<List<CertificateTemplateDTO>> getAllCertificateTemplates() {
        return ResponseEntity.ok(certificateTemplateService.getAllCertificateTemplates());
    }

    @GetMapping("/{certificateTemplateId}")
    ResponseEntity<CertificateTemplateDTO> getCertificateTemplate(@PathVariable UUID certificateTemplateId) {
        return ResponseEntity.ok(certificateTemplateService.getCertificateTemplate(certificateTemplateId));
    }

    @PutMapping("/{certificateTemplateId}")
    ResponseEntity<CertificateTemplateDTO> updateCertificateTemplate(@PathVariable UUID certificateTemplateId,
            @RequestBody CertificateTemplateDTO certificateTemplateDto) {
        return ResponseEntity.ok(
                certificateTemplateService.updateCertificateTemplate(certificateTemplateId, certificateTemplateDto));
    }

    @DeleteMapping("/{certificateTemplateId}")
    ResponseEntity<Void> deleteCertificateTemplate(@PathVariable UUID certificateTemplateId) {
        certificateTemplateService.deleteCertificateTemplate(certificateTemplateId);
        return ResponseEntity.noContent().build();
    }
}
