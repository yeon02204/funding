package com.funding.funding.domain.project.service.create;

import java.io.IOException;
import java.util.UUID;

import com.funding.funding.global.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/*
 * S3 저장 방식
 * 파일 검증 (20MB, 매직 바이트)
 * UUID 파일명 생성
 * S3 버킷 projects/ 하위에 업로드
 * https://{bucket}.s3.{region}.amazonaws.com/projects/{uuid}.png 반환 → DB 저장
 */
@Service
public class ImageStorageService {

    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024; // 20MB
    private static final String S3_KEY_PREFIX = "projects/";

    private static final byte[] JPG_MAGIC  = {(byte)0xFF, (byte)0xD8, (byte)0xFF};
    private static final byte[] PNG_MAGIC  = {(byte)0x89, 0x50, 0x4E, 0x47};
    private static final byte[] GIF_MAGIC  = {0x47, 0x49, 0x46, 0x38};
    private static final byte[] WEBP_MAGIC = {0x52, 0x49, 0x46, 0x46};

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.region}")
    private String region;

    public ImageStorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String save(MultipartFile file) {
        validate(file);

        try {
            String originalName = file.getOriginalFilename();
            String extension = extractExtension(originalName);
            String s3Key = S3_KEY_PREFIX + UUID.randomUUID() + extension;

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(s3Key)
                    .contentType(detectContentType(file.getBytes()))
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));

            return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + s3Key;

        } catch (IOException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 저장에 실패했습니다.");
        }
    }

    // ────────────────────────────────────────
    // 검증
    // ────────────────────────────────────────

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "파일이 비어 있습니다.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "파일 크기는 20MB를 초과할 수 없습니다.");
        }
        if (!isRealImageFile(file)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "이미지 파일(JPG, PNG, GIF, WEBP)만 업로드 가능합니다.");
        }
    }

    private boolean isRealImageFile(MultipartFile file) {
        try {
            byte[] header = file.getBytes();
            if (header.length < 4) return false;
            return startsWith(header, JPG_MAGIC)
                    || startsWith(header, PNG_MAGIC)
                    || startsWith(header, GIF_MAGIC)
                    || startsWith(header, WEBP_MAGIC);
        } catch (IOException e) {
            return false;
        }
    }

    private boolean startsWith(byte[] data, byte[] magic) {
        if (data.length < magic.length) return false;
        for (int i = 0; i < magic.length; i++) {
            if (data[i] != magic[i]) return false;
        }
        return true;
    }

    // ────────────────────────────────────────
    // 유틸
    // ────────────────────────────────────────

    private String extractExtension(String originalName) {
        if (originalName == null || originalName.isBlank()) return "";
        int idx = originalName.lastIndexOf(".");
        return idx == -1 ? "" : originalName.substring(idx);
    }

    private String detectContentType(byte[] header) {
        if (startsWith(header, JPG_MAGIC))  return "image/jpeg";
        if (startsWith(header, PNG_MAGIC))  return "image/png";
        if (startsWith(header, GIF_MAGIC))  return "image/gif";
        if (startsWith(header, WEBP_MAGIC)) return "image/webp";
        return "application/octet-stream";
    }
}