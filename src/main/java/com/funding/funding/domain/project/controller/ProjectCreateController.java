package com.funding.funding.domain.project.controller;

import java.net.URI;
import java.util.List;

import com.funding.funding.global.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.funding.funding.domain.project.dto.ProjectCreateRequest;
import com.funding.funding.domain.project.service.create.ProjectCreateService;

@RestController
@RequestMapping("/api/projects")
public class ProjectCreateController {

    private final ProjectCreateService projectCreateService;

    public ProjectCreateController(ProjectCreateService projectCreateService) {
        this.projectCreateService = projectCreateService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> createProject(
            @RequestPart("request") ProjectCreateRequest request,
            @RequestPart("images") List<MultipartFile> images,
            Authentication auth // ✅ JWT에서 userId 추출 (기존엔 request에서 ownerId를 직접 받았음)
    ) {
        Long userId = extractUserId(auth);
        Long projectId = projectCreateService.create(userId, request, images);

        return ResponseEntity
                .created(URI.create("/api/projects/" + projectId))
                .body(projectId);
    }

    // ✅ JWT 토큰에서 userId를 꺼내는 공통 메서드
    //    Authentication 객체는 JwtAuthenticationFilter에서 SecurityContext에 세팅해둔 것
    private Long extractUserId(Authentication auth) {
        if (auth == null || auth.getPrincipal() == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof Long id) return id;
        if (principal instanceof String s) return Long.valueOf(s);
        throw new ApiException(HttpStatus.UNAUTHORIZED, "인증 정보가 올바르지 않습니다.");
    }

    //  Swagger 테스트용 임시 API는 배포 시 제거할 것
    // (현재는 불필요하므로 삭제)
}