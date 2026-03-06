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

    @GetMapping("/{certificateId}")
    ResponseEntity<CertificateDTO> getCertificate(@PathVariable UUID certificateId) {
        return ResponseEntity.ok(certificateService.getCertificate(certificateId));
    }
}
