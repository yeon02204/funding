package com.funding.funding.domain.project.service.create;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import com.funding.funding.global.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/*
imageStorageService가 하는 일
파일 검증(20MB 제한)
파일 이름(UUID 생성)
로컬 폴더에 저장
/uploads/projects/파일이름.png를 반환 -> DB에 저장
*/

@Service
public class ImageStorageService {

    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024; // 20MB
    private static final String UPLOAD_DIR = "uploads/projects/";

    // ✅ 실제 이미지 파일 매직 바이트 (파일 앞 몇 바이트로 진짜 이미지인지 판별)
    //    contentType은 클라이언트가 조작 가능 → 바이트로 직접 확인해야 안전
    //    예) .exe 파일을 image/jpeg 라고 속여서 올리는 공격 방지
    private static final byte[] JPG_MAGIC  = {(byte)0xFF, (byte)0xD8, (byte)0xFF};
    private static final byte[] PNG_MAGIC  = {(byte)0x89, 0x50, 0x4E, 0x47};
    private static final byte[] GIF_MAGIC  = {0x47, 0x49, 0x46, 0x38};
    private static final byte[] WEBP_MAGIC = {0x52, 0x49, 0x46, 0x46}; // RIFF

    public String save(MultipartFile file) {
        validate(file);

        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);

            if (Files.notExists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalName = file.getOriginalFilename();
            String extension = extractExtension(originalName);
            String savedName = UUID.randomUUID() + extension;

            Path target = uploadPath.resolve(savedName);

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
            }

            return "/uploads/projects/" + savedName;

        } catch (IOException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 저장에 실패했습니다.");
        }
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "파일이 비어 있습니다.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "파일 크기는 20MB를 초과할 수 없습니다.");
        }

        // ✅ contentType 대신 실제 파일 바이트로 이미지 여부 검증
        //    contentType은 클라이언트가 마음대로 바꿀 수 있어서 신뢰할 수 없음
        if (!isRealImageFile(file)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "이미지 파일(JPG, PNG, GIF, WEBP)만 업로드 가능합니다.");
        }
    }

    // 파일 앞부분 바이트가 이미지 형식의 매직 바이트와 일치하는지 확인
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

    private String extractExtension(String originalName) {
        if (originalName == null || originalName.isBlank()) {
            return "";
        }

        int lastDotIndex = originalName.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }

        return originalName.substring(lastDotIndex);
    }
}