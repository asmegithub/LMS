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

    @GetMapping
    ResponseEntity<List<CertificateDTO>> getMyCertificates() {
        return ResponseEntity.ok(certificateService.getMyCertificates());
    }

    /** Admin: list all certificates */
    @GetMapping("/all")
    ResponseEntity<List<CertificateDTO>> getAllCertificates() {
        return ResponseEntity.ok(certificateService.getAllCertificates());
    }

    /** Admin: generate a sample certificate PNG */
    @PostMapping("/sample")
    ResponseEntity<CertificateDTO> generateSample() {
        return ResponseEntity.ok(certificateService.generateSampleCertificate());
    }

    /** Admin: delete a sample certificate (never deletes real certificates). */
    @DeleteMapping("/{certificateId}/sample")
    ResponseEntity<Void> deleteSample(@PathVariable UUID certificateId) {
        certificateService.deleteSampleCertificate(certificateId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{certificateId}")
    ResponseEntity<CertificateDTO> getCertificate(@PathVariable UUID certificateId) {
        return ResponseEntity.ok(certificateService.getCertificate(certificateId));
    }
}
