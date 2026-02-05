package com.EGM.LMS.controller;

import com.EGM.LMS.dto.CertificateDTO;
import com.EGM.LMS.service.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/certificates")
public class CertificateController {
    private final CertificateService certificateService;

    @PostMapping
    ResponseEntity<CertificateDTO> createCertificate(@RequestBody CertificateDTO certificateDto) {
        return ResponseEntity.ok(certificateService.createCertificate(certificateDto));
    }

    @GetMapping
    ResponseEntity<List<CertificateDTO>> getAllCertificates() {
        return ResponseEntity.ok(certificateService.getAllCertificates());
    }

    @GetMapping("/{certificateId}")
    ResponseEntity<CertificateDTO> getCertificate(@PathVariable UUID certificateId) {
        return ResponseEntity.ok(certificateService.getCertificate(certificateId));
    }

    @PutMapping("/{certificateId}")
    ResponseEntity<CertificateDTO> updateCertificate(@PathVariable UUID certificateId, @RequestBody CertificateDTO certificateDto) {
        return ResponseEntity.ok(certificateService.updateCertificate(certificateId, certificateDto));
    }

    @DeleteMapping("/{certificateId}")
    ResponseEntity<Void> deleteCertificate(@PathVariable UUID certificateId) {
        certificateService.deleteCertificate(certificateId);
        return ResponseEntity.noContent().build();
    }
}
