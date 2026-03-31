package com.EGM.LMS.controller;

import com.EGM.LMS.dto.ReferralBalanceDTO;
import com.EGM.LMS.dto.WithdrawalRequestDTO;
import com.EGM.LMS.service.ReferralBalanceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
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
@RequestMapping("/api/referral-balance")
public class ReferralBalanceController {

    private final ReferralBalanceService referralBalanceService;
    private final Path receiptUploadPath;
    private final Path legacyPayoutReceiptUploadPath;

    public ReferralBalanceController(
            ReferralBalanceService referralBalanceService,
            @Value("${app.referral-withdrawal-receipts.upload-dir:uploads/referral-withdrawal-receipts}") String uploadDir,
            @Value("${app.payout-receipts.upload-dir:uploads/payout-receipts}") String legacyPayoutUploadDir
    ) {
        this.referralBalanceService = referralBalanceService;
        this.receiptUploadPath = Path.of(uploadDir).toAbsolutePath().normalize();
        this.legacyPayoutReceiptUploadPath = Path.of(legacyPayoutUploadDir).toAbsolutePath().normalize();
    }

    @GetMapping("/me")
    public ResponseEntity<ReferralBalanceDTO> getMyBalance() {
        return ResponseEntity.ok(referralBalanceService.getMyBalance());
    }

    @PostMapping("/withdraw")
    public ResponseEntity<WithdrawalRequestDTO> requestWithdrawal(@RequestBody Map<String, Object> body) {
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
        String payoutDetailsJson = null;
        if (body.get("payoutDetailsJson") != null) {
            payoutDetailsJson = String.valueOf(body.get("payoutDetailsJson"));
        }
        return ResponseEntity.ok(referralBalanceService.requestWithdrawal(amount, methodOptionId, payoutDetailsJson));
    }

    @PostMapping("/withdraw/resubmit/{requestId}")
    public ResponseEntity<WithdrawalRequestDTO> resubmit(
            @PathVariable UUID requestId,
            @RequestBody Map<String, Object> body
    ) {
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
        String payoutDetailsJson = body.get("payoutDetailsJson") != null ? String.valueOf(body.get("payoutDetailsJson")) : null;
        return ResponseEntity.ok(referralBalanceService.resubmitWithdrawal(requestId, amount, methodOptionId, payoutDetailsJson));
    }

    @PostMapping("/withdrawals/{requestId}/report-issue")
    public ResponseEntity<WithdrawalRequestDTO> reportReceiptIssue(
            @PathVariable UUID requestId,
            @RequestBody(required = false) Map<String, Object> body
    ) {
        String message = body != null && body.get("message") != null ? String.valueOf(body.get("message")) : null;
        return ResponseEntity.ok(referralBalanceService.reportWithdrawalReceiptIssue(requestId, message));
    }

    @GetMapping("/withdrawals")
    public ResponseEntity<List<WithdrawalRequestDTO>> getMyWithdrawals() {
        return ResponseEntity.ok(referralBalanceService.getMyWithdrawals());
    }

    /** Admin: pending student referral withdrawal requests. */
    @GetMapping("/admin/pending-withdrawals")
    public ResponseEntity<List<WithdrawalRequestDTO>> getPendingWithdrawalsForAdmin() {
        return ResponseEntity.ok(referralBalanceService.getPendingWithdrawalsForAdmin());
    }

    @PostMapping(value = "/admin/withdrawals/{requestId}/approve", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<WithdrawalRequestDTO> approveWithdrawal(
            @PathVariable UUID requestId,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) throws IOException {
        StoredFile stored = null;
        if (file != null && !file.isEmpty()) {
            stored = storeReceiptFile(file);
        }
        return ResponseEntity.ok(referralBalanceService.approveWithdrawal(
                requestId,
                stored != null ? stored.storedFileName : null,
                stored != null ? stored.originalFileName : null));
    }

    @PostMapping("/admin/withdrawals/{requestId}/reject")
    public ResponseEntity<WithdrawalRequestDTO> rejectWithdrawal(
            @PathVariable UUID requestId,
            @RequestBody(required = false) Map<String, Object> body
    ) {
        var reason = body != null && body.get("reason") != null ? String.valueOf(body.get("reason")) : null;
        return ResponseEntity.ok(referralBalanceService.rejectWithdrawal(requestId, reason));
    }

    @GetMapping("/withdrawals/{requestId}/receipt")
    public ResponseEntity<byte[]> getWithdrawalReceipt(@PathVariable UUID requestId) throws IOException {
        WithdrawalRequestDTO dto = referralBalanceService.getWithdrawalById(requestId);
        if (dto.getReceiptUrl() == null || dto.getReceiptUrl().isBlank()) {
            return ResponseEntity.notFound().build();
        }
        String filename = dto.getReceiptUrl().substring(dto.getReceiptUrl().lastIndexOf('/') + 1);
        Path path = receiptUploadPath.resolve(filename);
        if (!Files.isRegularFile(path)) {
            // Backward compatibility: some older receipts may live in payout-receipts dir.
            Path legacyPath = legacyPayoutReceiptUploadPath.resolve(filename);
            if (!Files.isRegularFile(legacyPath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            path = legacyPath;
        }
        byte[] bytes = Files.readAllBytes(path);
        String contentType = Files.probeContentType(path);
        if (contentType == null || contentType.isBlank()) {
            contentType = MimeTypeUtils.IMAGE_JPEG_VALUE;
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" +
                        (dto.getReceiptOriginalFileName() != null ? dto.getReceiptOriginalFileName() : filename) + "\"")
                .body(bytes);
    }

    private record StoredFile(String storedFileName, String originalFileName) {}

    private StoredFile storeReceiptFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty.");
        }
        var contentType = file.getContentType() != null ? file.getContentType().toLowerCase() : "";
        String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename().toLowerCase() : "";
        boolean isImage = contentType.startsWith("image/") || originalName.endsWith(".png") || originalName.endsWith(".jpg") || originalName.endsWith(".jpeg") || originalName.endsWith(".webp");
        boolean isPdf = "application/pdf".equals(contentType) || originalName.endsWith(".pdf");
        if (!isImage && !isPdf) {
            throw new IllegalArgumentException("Only image or PDF receipt files are allowed.");
        }
        Files.createDirectories(receiptUploadPath);
        String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "file" : file.getOriginalFilename());
        String extension = "";
        int dotIndex = original.lastIndexOf('.');
        if (dotIndex >= 0 && dotIndex < original.length() - 1) {
            extension = original.substring(dotIndex);
        }
        String stored = UUID.randomUUID() + extension;
        Path target = receiptUploadPath.resolve(stored);
        try (var inputStream = file.getInputStream()) {
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
        }
        return new StoredFile(stored, original);
    }
}
