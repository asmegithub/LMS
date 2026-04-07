package com.EGM.LMS.controller;

import com.EGM.LMS.dto.InstructorPayoutRequestDTO;
import com.EGM.LMS.service.InstructorPayoutRequestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/instructor-payouts")
public class InstructorPayoutRequestController {

    private final InstructorPayoutRequestService instructorPayoutRequestService;
    private final Path uploadPath;

    public InstructorPayoutRequestController(
            InstructorPayoutRequestService instructorPayoutRequestService,
            @Value("${app.payout-receipts.upload-dir:uploads/payout-receipts}") String uploadDir) {
        this.instructorPayoutRequestService = instructorPayoutRequestService;
        this.uploadPath = Path.of(uploadDir).toAbsolutePath().normalize();
    }

    @GetMapping("/me")
    public ResponseEntity<List<InstructorPayoutRequestDTO>> getMyPayoutRequests() {
        return ResponseEntity.ok(instructorPayoutRequestService.getMyPayoutRequests());
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('payouts.manage')")
    public ResponseEntity<List<InstructorPayoutRequestDTO>> getPending() {
        return ResponseEntity.ok(instructorPayoutRequestService.getPending());
    }

    @PostMapping("/request")
    public ResponseEntity<InstructorPayoutRequestDTO> requestPayout(@RequestBody Map<String, Object> body) {
        var amountObj = body.get("amount");
        BigDecimal amount;
        if (amountObj instanceof Number n) {
            amount = BigDecimal.valueOf(n.doubleValue());
        } else if (amountObj instanceof String s) {
            amount = new BigDecimal(s);
        } else {
            return ResponseEntity.badRequest().build();
        }
        UUID bankDetailId = null;
        if (body.get("bankDetailId") != null) {
            if (body.get("bankDetailId") instanceof String str) {
                bankDetailId = UUID.fromString(str);
            }
        }
        UUID methodOptionId = null;
        if (body.get("methodOptionId") instanceof String str) {
            methodOptionId = UUID.fromString(str);
        }
        String payoutDetailsJson = body.get("payoutDetailsJson") != null ? String.valueOf(body.get("payoutDetailsJson"))
                : null;
        return ResponseEntity.ok(
                instructorPayoutRequestService.requestPayout(amount, bankDetailId, methodOptionId, payoutDetailsJson));
    }

    @PostMapping("/resubmit/{requestId}")
    public ResponseEntity<InstructorPayoutRequestDTO> resubmit(@PathVariable UUID requestId,
            @RequestBody Map<String, Object> body) {
        var amountObj = body.get("amount");
        BigDecimal amount;
        if (amountObj instanceof Number n) {
            amount = BigDecimal.valueOf(n.doubleValue());
        } else if (amountObj instanceof String s) {
            amount = new BigDecimal(s);
        } else {
            return ResponseEntity.badRequest().build();
        }
        UUID methodOptionId = null;
        if (body.get("methodOptionId") instanceof String str) {
            methodOptionId = UUID.fromString(str);
        }
        String payoutDetailsJson = body.get("payoutDetailsJson") != null ? String.valueOf(body.get("payoutDetailsJson"))
                : null;
        return ResponseEntity
                .ok(instructorPayoutRequestService.resubmit(requestId, amount, methodOptionId, payoutDetailsJson));
    }

    @PostMapping(value = "/approve/{requestId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('payouts.manage')")
    public ResponseEntity<InstructorPayoutRequestDTO> approve(
            @PathVariable UUID requestId,
            @RequestParam("file") MultipartFile file) throws IOException {
        var stored = storeFile(file);
        return ResponseEntity
                .ok(instructorPayoutRequestService.approve(requestId, stored.storedFileName, stored.originalFileName));
    }

    @PostMapping("/reject/{requestId}")
    @PreAuthorize("hasAuthority('payouts.manage')")
    public ResponseEntity<InstructorPayoutRequestDTO> reject(@PathVariable UUID requestId,
            @RequestBody Map<String, Object> body) {
        var reason = body != null && body.get("reason") != null ? String.valueOf(body.get("reason")) : null;
        return ResponseEntity.ok(instructorPayoutRequestService.reject(requestId, reason));
    }

    @GetMapping("/{requestId}/receipt")
    @PreAuthorize("hasAuthority('payouts.manage')")
    ResponseEntity<byte[]> getReceipt(@PathVariable UUID requestId) throws IOException {
        InstructorPayoutRequestDTO dto = instructorPayoutRequestService.getById(requestId);
        if (dto.getReceiptUrl() == null || dto.getReceiptUrl().isBlank()) {
            return ResponseEntity.notFound().build();
        }
        String filename = dto.getReceiptUrl().substring(dto.getReceiptUrl().lastIndexOf('/') + 1);
        Path path = uploadPath.resolve(filename);
        if (!Files.isRegularFile(path)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        byte[] bytes = Files.readAllBytes(path);
        String contentType = Files.probeContentType(path);
        if (contentType == null || contentType.isBlank()) {
            contentType = MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE;
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\""
                        + (dto.getReceiptOriginalFileName() != null ? dto.getReceiptOriginalFileName() : filename)
                        + "\"")
                .body(bytes);
    }

    private record StoredFile(String storedFileName, String originalFileName) {
    }

    private StoredFile storeFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty.");
        }
        Files.createDirectories(uploadPath);
        String original = org.springframework.util.StringUtils
                .cleanPath(file.getOriginalFilename() == null ? "file" : file.getOriginalFilename());
        String extension = "";
        int dotIndex = original.lastIndexOf('.');
        if (dotIndex >= 0 && dotIndex < original.length() - 1) {
            extension = original.substring(dotIndex);
        }
        String stored = java.util.UUID.randomUUID() + extension;
        Path target = uploadPath.resolve(stored);
        try (var inputStream = file.getInputStream()) {
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
        }
        return new StoredFile(stored, original);
    }
}
