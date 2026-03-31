package com.EGM.LMS.service.impl;
import com.EGM.LMS.dto.CertificateDTO;
import com.EGM.LMS.dto.CertificateTemplateDTO;
import com.EGM.LMS.dto.CourseDTO;
import com.EGM.LMS.dto.EnrollmentDTO;
import com.EGM.LMS.dto.UserDTO;
import com.EGM.LMS.model.Certificate;
import com.EGM.LMS.repository.CertificateRepository;
import com.EGM.LMS.repository.CertificateTemplateRepository;
import com.EGM.LMS.repository.CourseRepository;
import com.EGM.LMS.repository.EnrollmentRepository;
import com.EGM.LMS.repository.UserRepository;
import com.EGM.LMS.service.CertificateService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Service
public class CertificateServiceImpl implements CertificateService {
    private final CertificateRepository certificateRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final CertificateTemplateRepository certificateTemplateRepository;

    private final Path certificateUploadPath;
    private final Path mediaUploadPath;
    private final Font ethiopicRegular;
    private final Font ethiopicBold;
    private final String certificateSignatoryName;

    public CertificateServiceImpl(
            CertificateRepository certificateRepository,
            EnrollmentRepository enrollmentRepository,
            UserRepository userRepository,
            CourseRepository courseRepository,
            CertificateTemplateRepository certificateTemplateRepository,
            @Value("${app.certificates.upload-dir:uploads/certificates}") String uploadDir,
            @Value("${app.media.upload-dir:uploads}") String mediaUploadDir,
            @Value("${app.certificates.signatory-name:}") String certificateSignatoryName
    ) {
        this.certificateRepository = certificateRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.certificateTemplateRepository = certificateTemplateRepository;
        this.certificateUploadPath = Path.of(uploadDir).toAbsolutePath().normalize();
        this.mediaUploadPath = Path.of(mediaUploadDir).toAbsolutePath().normalize();
        this.ethiopicRegular = loadFont("/fonts/NotoSansEthiopic-Regular.ttf", new Font("SansSerif", Font.PLAIN, 24));
        this.ethiopicBold = loadFont("/fonts/NotoSansEthiopic-Bold.ttf", new Font("SansSerif", Font.BOLD, 24));
        this.certificateSignatoryName = certificateSignatoryName == null ? "" : certificateSignatoryName.trim();
    }

    @Override
    public CertificateDTO createCertificate(CertificateDTO certificate) {
        return toDto(certificateRepository.save(toEntity(certificate)));
    }

    @Override
    public List<CertificateDTO> getMyCertificates() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            throw new IllegalStateException("Authentication required");
        }
        var currentUser = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        List<Certificate> certificates = certificateRepository.findByStudent_IdOrderByIssuedAtDesc(currentUser.getId());
        return certificates.stream().map(this::toDto).toList();
    }

    @Override
    public List<CertificateDTO> getAllCertificates() {
        var certificates = certificateRepository.findAll();
        var certificateDtos = new java.util.ArrayList<CertificateDTO>();
        for (Certificate certificate : certificates) {
            certificateDtos.add(toDto(certificate));
        }
        return certificateDtos;
    }

    @Override
    public CertificateDTO getCertificate(UUID certificateId) {
        return toDto(certificateRepository.findById(certificateId).orElseThrow());
    }

    @Override
    public CertificateDTO issueForEnrollment(UUID enrollmentId) {
        if (enrollmentId == null) {
            throw new IllegalArgumentException("Enrollment id is required");
        }

        var enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found"));

        if (!enrollment.isCompleted()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enrollment is not completed");
        }

        // Only the owner (student) or admin can request issuance.
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        var currentUser = userRepository.findByEmail(auth.getName()).orElse(null);
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        }
        var isAdmin = "ADMIN".equalsIgnoreCase(currentUser.getRole());
        if (!isAdmin && (enrollment.getStudent() == null || !enrollment.getStudent().getId().equals(currentUser.getId()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed");
        }

        var existing = certificateRepository.findFirstByEnrollment_Id(enrollmentId);
        if (existing.isPresent()) {
            return toDto(existing.get());
        }

        var student = enrollment.getStudent();
        var course = enrollment.getCourse();
        if (student == null || course == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enrollment must have student and course");
        }

        var now = java.time.LocalDateTime.now();
        var certificateNumber = "EGM-" + now.getYear() + "-" + String.valueOf(UUID.randomUUID()).substring(0, 8).toUpperCase();
        var verificationCode = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();

        var cert = Certificate.builder()
                .enrollment(enrollment)
                .student(student)
                .course(course)
                .template(certificateTemplateRepository.findFirstByIsDefaultTrueAndIsActiveTrue().orElse(null))
                .certificateNumber(certificateNumber)
                .verificationCode(verificationCode)
                .issuedAt(now)
                .build();
        // NOTE: Lombok builder uses field names; keep save before URL assignment.
        cert = certificateRepository.save(cert);

        var storedFileName = certificateNumber + ".png";
        try {
            Files.createDirectories(certificateUploadPath);
            var filePath = certificateUploadPath.resolve(storedFileName);
            generateCertificatePng(
                    filePath,
                    student.getFirstName(),
                    student.getLastName(),
                    course.getTitle(),
                    certificateNumber,
                    verificationCode,
                    now.toLocalDate().toString(),
                    cert.getTemplate() != null ? cert.getTemplate().getBackgroundUrl() : null
            );
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate certificate");
        }

        cert.setCertificateUrl(ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/certificates/")
                .path(storedFileName)
                .toUriString());
        cert = certificateRepository.save(cert);
        return toDto(cert);
    }

    @Override
    public CertificateDTO generateSampleCertificate() {
        // Admin-only
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        var currentUser = userRepository.findByEmail(auth.getName()).orElse(null);
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        }
        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin only");
        }

        var now = java.time.LocalDateTime.now();
        var certificateNumber = "SAMPLE-" + now.getYear() + "-" + String.valueOf(UUID.randomUUID()).substring(0, 8).toUpperCase();
        var verificationCode = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();

        var cert = Certificate.builder()
                .enrollment(null)
                .student(null)
                .course(null)
                .template(certificateTemplateRepository.findFirstByIsDefaultTrueAndIsActiveTrue().orElse(null))
                .certificateNumber(certificateNumber)
                .verificationCode(verificationCode)
                .issuedAt(now)
                .build();
        cert = certificateRepository.save(cert);

        var storedFileName = certificateNumber + ".png";
        try {
            Files.createDirectories(certificateUploadPath);
            var filePath = certificateUploadPath.resolve(storedFileName);
            generateCertificatePng(
                    filePath,
                    "Sample",
                    "Student",
                    "Sample Course",
                    certificateNumber,
                    verificationCode,
                    now.toLocalDate().toString(),
                    cert.getTemplate() != null ? cert.getTemplate().getBackgroundUrl() : null
            );
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate certificate");
        }

        cert.setCertificateUrl(ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/certificates/")
                .path(storedFileName)
                .toUriString());
        cert = certificateRepository.save(cert);
        return toDto(cert);
    }

    @Override
    public void deleteSampleCertificate(UUID certificateId) {
        if (certificateId == null) return;

        // Admin-only
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        var currentUser = userRepository.findByEmail(auth.getName()).orElse(null);
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        }
        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin only");
        }

        var cert = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Certificate not found"));

        boolean isSample = cert.getEnrollment() == null
                && cert.getStudent() == null
                && cert.getCourse() == null
                && cert.getCertificateNumber() != null
                && cert.getCertificateNumber().startsWith("SAMPLE-");

        if (!isSample) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only sample certificates can be deleted");
        }

        // Attempt to delete file (best-effort)
        try {
            var url = cert.getCertificateUrl();
            if (url != null && !url.isBlank()) {
                String filename = null;
                if (url.contains("/uploads/certificates/")) {
                    filename = url.substring(url.lastIndexOf("/uploads/certificates/") + "/uploads/certificates/".length());
                } else if (url.startsWith("/uploads/certificates/")) {
                    filename = url.substring("/uploads/certificates/".length());
                }
                if (filename != null && !filename.isBlank()) {
                    Files.deleteIfExists(certificateUploadPath.resolve(filename).normalize());
                }
            }
        } catch (Exception ignored) {
            // ignore file delete failures
        }

        certificateRepository.deleteById(certificateId);
    }

    @Override
    public CertificateDTO updateCertificate(UUID certificateId, CertificateDTO certificate) {
        certificateRepository.findById(certificateId).orElseThrow();
        var entity = toEntity(certificate);
        entity.setId(certificateId);
        return toDto(certificateRepository.save(entity));
    }

    @Override
    public void deleteCertificate(UUID certificateId) {
        certificateRepository.deleteById(certificateId);
    }

    private Certificate toEntity(CertificateDTO certificate) {
        var enrollmentId = certificate.getEnrollment() != null ? certificate.getEnrollment().getId() : null;
        var studentId = certificate.getStudent() != null ? certificate.getStudent().getId() : null;
        var courseId = certificate.getCourse() != null ? certificate.getCourse().getId() : null;
        var templateId = certificate.getTemplate() != null ? certificate.getTemplate().getId() : null;
        return Certificate.builder()
                .enrollment(enrollmentId != null ? enrollmentRepository.findById(enrollmentId).orElse(null) : null)
                .student(studentId != null ? userRepository.findById(studentId).orElse(null) : null)
                .course(courseId != null ? courseRepository.findById(courseId).orElse(null) : null)
                .template(templateId != null ? certificateTemplateRepository.findById(templateId).orElse(null) : null)
                .certificateNumber(certificate.getCertificateNumber())
                .certificateUrl(certificate.getCertificateUrl())
                .verificationCode(certificate.getVerificationCode())
                .issuedAt(certificate.getIssuedAt())
                .expiresAt(certificate.getExpiresAt())
                .build();
    }

    private CertificateDTO toDto(Certificate certificate) {
        return CertificateDTO.builder()
                .id(certificate.getId())
                .enrollment(certificate.getEnrollment() != null ? EnrollmentDTO.builder().id(certificate.getEnrollment().getId()).build() : null)
                .student(certificate.getStudent() != null ? UserDTO.builder()
                        .id(certificate.getStudent().getId())
                        .firstName(certificate.getStudent().getFirstName())
                        .lastName(certificate.getStudent().getLastName())
                        .email(certificate.getStudent().getEmail())
                        .build() : null)
                .course(certificate.getCourse() != null ? CourseDTO.builder()
                        .id(certificate.getCourse().getId())
                        .title(certificate.getCourse().getTitle())
                        .build() : null)
                .template(certificate.getTemplate() != null ? CertificateTemplateDTO.builder().id(certificate.getTemplate().getId()).build() : null)
                .certificateNumber(certificate.getCertificateNumber())
                .certificateUrl(certificate.getCertificateUrl())
                .verificationCode(certificate.getVerificationCode())
                .issuedAt(certificate.getIssuedAt())
                .expiresAt(certificate.getExpiresAt())
                .createdAt(certificate.getCreatedAt())
                .updatedAt(certificate.getUpdatedAt())
                .build();
    }

    private void generateCertificatePng(
            Path filePath,
            String firstName,
            String lastName,
            String courseTitle,
            String certificateNumber,
            String verificationCode,
            String issueDate,
            String backgroundUrl
    ) throws IOException {
        int width = 1600;
        int height = 1131;
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var g = image.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Background template image (if provided)
            BufferedImage templateBg = loadBackground(backgroundUrl);
            if (templateBg != null) {
                g.drawImage(templateBg, 0, 0, width, height, null);
                // subtle readability overlay
                g.setColor(new Color(255, 255, 255, 36));
                g.fillRect(0, 0, width, height);
            } else {
                g.setColor(new Color(250, 250, 252));
                g.fillRect(0, 0, width, height);

                // Border (fallback template)
                g.setColor(new Color(212, 175, 55)); // gold-ish
                g.setStroke(new BasicStroke(10f));
                g.drawRect(40, 40, width - 80, height - 80);
                g.setColor(new Color(30, 41, 59));
                g.setStroke(new BasicStroke(2f));
                g.drawRect(70, 70, width - 140, height - 140);
            }

            // Watermark text (rotated)
            var wm = "ኢግሚ ቤተ ጉባኤ";
            var wmFont = ethiopicBold.deriveFont(90f);
            g.setFont(wmFont);
            g.setColor(new Color(0, 0, 0, 26));
            var oldTx = g.getTransform();
            var tx = new AffineTransform();
            tx.rotate(Math.toRadians(-18), width / 2.0, height / 2.0);
            g.setTransform(tx);
            drawCentered(g, wm, width / 2, height / 2);
            g.setTransform(oldTx);

            // If template has its own artwork, keep header minimal.
            if (templateBg == null) {
                g.setColor(new Color(212, 175, 55));
                g.fillOval(120, 120, 80, 80);
                g.setColor(Color.WHITE);
                g.setFont(new Font("DejaVu Sans", Font.BOLD, 28));
                drawCentered(g, "EGM", 160, 165);
            }

            // Orthodox-style decorative crosses (subtle overlay)
            g.setColor(new Color(120, 82, 28, templateBg != null ? 90 : 160));
            drawOrthodoxCross(g, 110, 120, 70);
            drawOrthodoxCross(g, width - 110, 120, 70);
            drawOrthodoxCross(g, 110, height - 120, 70);
            drawOrthodoxCross(g, width - 110, height - 120, 70);

            // --- Certificate content (Amharic) ---
            var studentName = ((firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName)).trim();
            if (studentName.isBlank()) studentName = "______________";
            var ct = courseTitle == null || courseTitle.isBlank() ? "______________" : courseTitle.trim();

            int contentLeft = 170;
            int contentRight = width - 170;
            int contentWidth = contentRight - contentLeft;
            int centerX = width / 2;

            // Typography scaled up (target closer to ~2x original look).
            // Footer remains fixed near the bottom, so keep enough vertical room for the body.
            int y = 155;
            g.setColor(new Color(15, 23, 42));

            // Title (larger to better occupy the paper)
            g.setFont(ethiopicBold.deriveFont(104f));
            y = drawWrappedCentered(g, "የምስክር ወረቀት", centerX, y, contentWidth, 100);

            y += 8;
            g.setFont(ethiopicRegular.deriveFont(48f));
            y = drawWrappedCentered(g, "ይህ የምስክር ወረቀት ለተከበሩ፡ " + studentName, centerX, y, contentWidth, 64);

            y += 6;
            // Course completion sentence, with course name emphasized on its own line
            y = drawWrappedCentered(g, "በኢግሚ ቤተ ጉባኤ ሲሰጥ የቆየውን የ", centerX, y, contentWidth, 64);
            g.setFont(ethiopicBold.deriveFont(60f));
            y = drawWrappedCentered(g, ct, centerX, y + 6, contentWidth, 76);
            g.setFont(ethiopicRegular.deriveFont(48f));
            y = drawWrappedCentered(g, "ትምህርት በሚገባ አጠናቀው ለዚህ ማዕረግ በመብቃታቸው የተሰጠ የምስክር ወረቀት ነው።", centerX, y + 4, contentWidth, 64);

            y += 6;
            g.setFont(ethiopicRegular.deriveFont(44f));
            y = drawWrappedCentered(
                    g,
                    "ተማሪው/ዋ በትምህርት ቆይታቸው ወቅት ያሳዩት ትጋት፣ መንፈሳዊ ሥነ-ምግባር እና የቀለም ዕውቀት ተመዝግቦላቸዋል። ለሚቀጥለው የዕውቀት ደረጃም ብቁ መሆናቸውን ቤተ ጉባኤው ያረጋግጣል።",
                    centerX,
                    y + 6,
                    contentWidth,
                    60
            );

            y += 8;
            g.setFont(ethiopicRegular.deriveFont(40f));
            y = drawWrappedCentered(g, "\"እግዚአብሔር ብርሃኔና መድኃኒቴ ነው\" (መዝሙር ፳፯:፩)", centerX, y + 6, contentWidth, 56);

            // Footer (requested)
            // Footer stays pinned at the bottom edge (as requested).
            int footerY = height - 220;
            g.setColor(new Color(30, 41, 59));
            g.setFont(ethiopicRegular.deriveFont(22f));
            g.drawString("የተሰጠበት ቀን፦ " + issueDate, contentLeft, footerY);
            g.drawString("የምስክር ወረቀት ቁጥር፦ " + certificateNumber, contentLeft, footerY + 38);

            String signatory = !certificateSignatoryName.isBlank() ? certificateSignatoryName : "__________________";
            g.setFont(ethiopicBold.deriveFont(22f));
            g.drawString(signatory, contentRight - 520, footerY);
            g.setFont(ethiopicRegular.deriveFont(20f));
            g.drawString("የጉባኤው መምህር / ኃላፊ", contentRight - 520, footerY + 34);
            g.setFont(ethiopicRegular.deriveFont(18f));
            g.drawString("(ፊርማና ማኅተም)", contentRight - 520, footerY + 62);
        } finally {
            g.dispose();
        }

        ImageIO.write(image, "png", filePath.toFile());
    }

    private BufferedImage loadBackground(String backgroundUrl) {
        if (backgroundUrl == null || backgroundUrl.isBlank()) return null;
        String u = backgroundUrl.trim();
        try {
            if (u.startsWith("http://") || u.startsWith("https://")) {
                return ImageIO.read(java.net.URI.create(u).toURL());
            }
            // If stored as "/uploads/xxx.png", map to local file under app.media.upload-dir
            if (u.startsWith("/uploads/")) {
                String relative = u.substring("/uploads/".length());
                Path p = mediaUploadPath.resolve(relative).normalize();
                if (Files.exists(p)) {
                    return ImageIO.read(p.toFile());
                }
                return null;
            }
            // If stored as "uploads/xxx.png" or relative file path
            Path p = Path.of(u);
            if (!p.isAbsolute()) {
                p = mediaUploadPath.resolve(u).normalize();
            }
            if (Files.exists(p)) {
                return ImageIO.read(p.toFile());
            }
        } catch (Exception ignored) {
            return null;
        }
        return null;
    }

    private Font loadFont(String classpathPath, Font fallback) {
        try (InputStream in = CertificateServiceImpl.class.getResourceAsStream(classpathPath)) {
            if (in == null) return fallback;
            Font base = Font.createFont(Font.TRUETYPE_FONT, in);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(base);
            return base;
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private void drawCentered(Graphics2D g, String text, int centerX, int y) {
        var fm = g.getFontMetrics();
        int x = centerX - fm.stringWidth(text) / 2;
        g.drawString(text, x, y);
    }

    private int drawWrappedCentered(Graphics2D g, String text, int centerX, int startY, int maxWidth, int lineHeight) {
        if (text == null) return startY;
        var fm = g.getFontMetrics();
        var words = text.trim().split("\\s+");
        StringBuilder line = new StringBuilder();
        int y = startY;
        for (String w : words) {
            if (line.length() == 0) {
                line.append(w);
                continue;
            }
            String candidate = line + " " + w;
            if (fm.stringWidth(candidate) <= maxWidth) {
                line.append(" ").append(w);
            } else {
                drawCentered(g, line.toString(), centerX, y);
                y += lineHeight;
                line = new StringBuilder(w);
            }
        }
        if (line.length() > 0) {
            drawCentered(g, line.toString(), centerX, y);
            y += lineHeight;
        }
        return y;
    }

    /**
     * Draw a simple Ethiopian-orthodox inspired cross using lines and small end-caps.
     * (We keep it simple and font-independent so it renders everywhere.)
     */
    private void drawOrthodoxCross(Graphics2D g, int centerX, int centerY, int size) {
        var oldStroke = g.getStroke();
        try {
            int half = size / 2;
            g.setStroke(new BasicStroke(Math.max(2f, size / 10f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Vertical + horizontal arms
            g.drawLine(centerX, centerY - half, centerX, centerY + half);
            g.drawLine(centerX - half, centerY, centerX + half, centerY);

            // Small diagonal accents (gives "Ethiopian" feel)
            int d = Math.max(10, size / 5);
            g.setStroke(new BasicStroke(Math.max(1.5f, size / 14f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(centerX - d, centerY - d, centerX + d, centerY + d);
            g.drawLine(centerX - d, centerY + d, centerX + d, centerY - d);

            // End caps (tiny circles)
            int r = Math.max(4, size / 14);
            g.fillOval(centerX - r, centerY - half - r, r * 2, r * 2);
            g.fillOval(centerX - r, centerY + half - r, r * 2, r * 2);
            g.fillOval(centerX - half - r, centerY - r, r * 2, r * 2);
            g.fillOval(centerX + half - r, centerY - r, r * 2, r * 2);
        } finally {
            g.setStroke(oldStroke);
        }
    }
}
