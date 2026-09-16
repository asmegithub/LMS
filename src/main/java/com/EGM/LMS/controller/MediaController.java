package com.EGM.LMS.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    private final Path uploadPath;

    public MediaController(@Value("${app.media.upload-dir:uploads}") String uploadDir) {
        this.uploadPath = Path.of(uploadDir).toAbsolutePath().normalize();
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "File is empty."));
        }

        Files.createDirectories(uploadPath);

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "file" : file.getOriginalFilename());
        String extension = "";
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex >= 0 && dotIndex < originalFileName.length() - 1) {
            extension = originalFileName.substring(dotIndex);
        }

        String storedFileName = UUID.randomUUID() + extension;
        Path targetPath = uploadPath.resolve(storedFileName);

        try (var inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }

        String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/")
                .path(storedFileName)
                .toUriString();

        return ResponseEntity.ok(Map.of(
                "url", url,
                "fileName", originalFileName
        ));
    }

    @GetMapping("/stream/{fileName:.+}")
    public ResponseEntity<ResourceRegion> streamVideo(
            @PathVariable String fileName,
            @RequestHeader HttpHeaders headers) throws IOException {

        String cleaned = StringUtils.cleanPath(fileName);
        if (cleaned.contains("..") || cleaned.contains("/") || cleaned.contains("\\")) {
            return ResponseEntity.badRequest().build();
        }

        Path videoPath = uploadPath.resolve(cleaned).normalize();
        if (!Files.exists(videoPath) || !Files.isRegularFile(videoPath)) {
            return ResponseEntity.notFound().build();
        }

        UrlResource video = new UrlResource(videoPath.toUri());
        long contentLength = video.contentLength();

        String mimeType = Files.probeContentType(videoPath);
        MediaType mediaType = (mimeType != null) ? MediaType.parseMediaType(mimeType) : MediaType.APPLICATION_OCTET_STREAM;

        // 1MB chunk size for smooth seek and buffering
        long chunkSize = 1024 * 1024;
        List<HttpRange> ranges = headers.getRange();

        if (!ranges.isEmpty()) {
            HttpRange range = ranges.get(0);
            long start = range.getRangeStart(contentLength);
            long end = range.getRangeEnd(contentLength);
            long rangeLength = Math.min(chunkSize, end - start + 1);
            ResourceRegion region = new ResourceRegion(video, start, rangeLength);

            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .contentType(mediaType)
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                    .header("Cache-Control", "private, no-transform, max-age=3600")
                    .header("X-Content-Type-Options", "nosniff")
                    .body(region);
        } else {
            long rangeLength = Math.min(chunkSize, contentLength);
            ResourceRegion region = new ResourceRegion(video, 0, rangeLength);

            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .contentType(mediaType)
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                    .header("Cache-Control", "private, no-transform, max-age=3600")
                    .header("X-Content-Type-Options", "nosniff")
                    .body(region);
        }
    }
}
