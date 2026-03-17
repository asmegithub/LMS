package com.EGM.LMS.controller;

import com.EGM.LMS.dto.PaymentProofDTO;
import com.EGM.LMS.service.PaymentProofService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
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
@RequestMapping("/api/payment-proofs")
public class PaymentProofController {

    private final PaymentProofService paymentProofService;
    private final Path uploadPath;

    public PaymentProofController(
            PaymentProofService paymentProofService,
            @Value("${app.payment-proofs.upload-dir:uploads/payment-proofs}") String uploadDir
    ) {
        this.paymentProofService = paymentProofService;
        this.uploadPath = Path.of(uploadDir).toAbsolutePath().normalize();
    }

    /** Student: submit proof for a single course. */
    @PostMapping(value = "/course/{courseId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<PaymentProofDTO> submitForCourse(
            @PathVariable UUID courseId,
            @RequestParam("paymentAccountId") UUID paymentAccountId,
            @RequestParam(value = "amount", required = false) String amount,
            @RequestParam(value = "currency", required = false) String currency,
            @RequestParam(value = "note", required = false) String note,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        var stored = storeFile(file);
        BigDecimal parsedAmount = parseAmount(amount);
        return ResponseEntity.ok(paymentProofService.submitForCourse(
                courseId,
                paymentAccountId,
                parsedAmount,
                currency,
                stored.storedFileName,
                stored.originalFileName,
                note
        ));
    }

    /** Student: submit proof for a cart order. */
    @PostMapping(value = "/order/{orderId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<PaymentProofDTO> submitForOrder(
            @PathVariable UUID orderId,
            @RequestParam("paymentAccountId") UUID paymentAccountId,
            @RequestParam(value = "amount", required = false) String amount,
            @RequestParam(value = "currency", required = false) String currency,
            @RequestParam(value = "note", required = false) String note,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        var stored = storeFile(file);
        BigDecimal parsedAmount = parseAmount(amount);
        return ResponseEntity.ok(paymentProofService.submitForOrder(
                orderId,
                paymentAccountId,
                parsedAmount,
                currency,
                stored.storedFileName,
                stored.originalFileName,
                note
        ));
    }

    /** Student: my proofs. */
    @GetMapping("/me")
    ResponseEntity<List<PaymentProofDTO>> myProofs() {
        return ResponseEntity.ok(paymentProofService.getMyProofs());
    }

    /** Admin: pending proofs. */
    @GetMapping("/pending")
    ResponseEntity<List<PaymentProofDTO>> pending() {
        return ResponseEntity.ok(paymentProofService.getPending());
    }

    /** Admin: approve proof. */
    @PostMapping("/{proofId}/approve")
    ResponseEntity<PaymentProofDTO> approve(@PathVariable UUID proofId) {
        return ResponseEntity.ok(paymentProofService.approve(proofId));
    }

    /** Admin: reject proof. */
    @PostMapping("/{proofId}/reject")
    ResponseEntity<PaymentProofDTO> reject(@PathVariable UUID proofId, @RequestBody Map<String, Object> body) {
        var reason = body != null && body.get("reason") != null ? String.valueOf(body.get("reason")) : null;
        return ResponseEntity.ok(paymentProofService.reject(proofId, reason));
    }

    @GetMapping("/{proofId}")
    ResponseEntity<PaymentProofDTO> getById(@PathVariable UUID proofId) {
        return ResponseEntity.ok(paymentProofService.getById(proofId));
    }

    private record StoredFile(String storedFileName, String originalFileName) {}

    private StoredFile storeFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty.");
        }
        Files.createDirectories(uploadPath);
        String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "file" : file.getOriginalFilename());
        String extension = "";
        int dotIndex = original.lastIndexOf('.');
        if (dotIndex >= 0 && dotIndex < original.length() - 1) {
            extension = original.substring(dotIndex);
        }
        String stored = UUID.randomUUID() + extension;
        Path target = uploadPath.resolve(stored);
        try (var inputStream = file.getInputStream()) {
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
        }
        return new StoredFile(stored, original);
    }

    private BigDecimal parseAmount(String amount) {
        if (amount == null || amount.isBlank()) return null;
        try {
            return new BigDecimal(amount.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid amount");
        }
    }
}

