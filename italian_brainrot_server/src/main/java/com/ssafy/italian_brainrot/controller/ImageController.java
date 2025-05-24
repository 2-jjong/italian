package com.ssafy.italian_brainrot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/image")
public class ImageController {

    private static final Logger log = LoggerFactory.getLogger(ImageController.class);
    private static final String UPLOAD_DIR = "C:\\Users\\user\\Downloads";

    /**
     * 이미지 저장 API
     * form-data: fileName(파일명), image(이미지파일)
     * 반환: /image/fileName
     */
    @PostMapping("")
    public ResponseEntity<String> uploadImage(
            @RequestParam("fileName") String fileName,
            @RequestParam("image") MultipartFile image) {

        try {
            // 파일 유효성 검사
            if (image.isEmpty()) {
                return ResponseEntity.badRequest().body("이미지 파일이 비어있습니다.");
            }

            // Content-Type 검증 (이미지 파일인지 확인)
            String contentType = image.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body("이미지 파일만 업로드 가능합니다.");
            }

            log.debug("업로드 요청: fileName={}, fileSize={}, contentType={}",
                    fileName, image.getSize(), image.getContentType());

            // 원본 파일명에서 확장자 추출
            String originalFileName = image.getOriginalFilename();
            String fileExtension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }

            // fileName에 확장자가 없다면 원본 파일의 확장자 추가
            String finalFileName = fileName;
            if (!fileName.contains(".") && !fileExtension.isEmpty()) {
                finalFileName = fileName + fileExtension;
            }

            log.debug("최종 파일명: {}, 확장자: {}", finalFileName, fileExtension);

            // 업로드 디렉토리 생성
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.debug("디렉토리 생성: {}", uploadPath.toAbsolutePath());
            }

            // 파일 저장 (Files.copy 사용으로 더 안전한 저장)
            Path filePath = uploadPath.resolve(finalFileName);
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            log.debug("파일 저장 완료: {} (크기: {})", filePath.toAbsolutePath(), image.getSize());

            // URL 반환 (실제 저장된 파일명 사용)
            String imageUrl = "/image/" + finalFileName;
            log.debug("이미지 저장 완료: {}", imageUrl);

            return ResponseEntity.ok(imageUrl);

        } catch (IOException e) {
            log.error("이미지 저장 실패: fileName={}", fileName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("이미지 저장에 실패했습니다.");
        }
    }

    /**
     * 이미지 조회 API
     * 파라미터: imageURL (예: /image/example.jpg)
     * 반환: 실제 이미지 파일
     */
    @GetMapping("/{imageURL}")
    public ResponseEntity<Resource> getImage(@PathVariable("imageURL") String imageURL) {

        try {
            // 파일 경로 생성
            Path filePath = Paths.get(UPLOAD_DIR, imageURL);
            File file = filePath.toFile();

            // 파일 존재 확인
            if (!file.exists() || !file.isFile()) {
                log.warn("파일을 찾을 수 없음: {}", imageURL);
                return ResponseEntity.notFound().build();
            }

            // 파일 리소스 생성
            Resource resource = new FileSystemResource(file);

            // Content-Type 설정
            String contentType = determineContentType(imageURL);

            log.debug("이미지 조회: {} (ContentType: {})", imageURL, contentType);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CACHE_CONTROL, "max-age=3600")
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + imageURL + "\"")
                    .body(resource);

        } catch (Exception e) {
            log.error("이미지 조회 실패: imageURL={}", imageURL, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 파일 확장자로 Content-Type 결정
     */
    private String determineContentType(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "application/octet-stream";
        }

        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "bmp" -> "image/bmp";
            case "webp" -> "image/webp";
            case "svg" -> "image/svg+xml";
            case "ico" -> "image/x-icon";
            default -> "application/octet-stream";
        };
    }
}