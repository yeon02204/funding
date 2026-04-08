package com.funding.funding.domain.project.controller;

import com.funding.funding.domain.project.dto.ProjectUpdateRequest;
import com.funding.funding.domain.project.service.update.ProjectUpdateService;
import com.funding.funding.global.exception.ApiException;
import com.funding.funding.global.response.ApiResponse;
import com.funding.funding.global.security.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
public class ProjectUpdateController {

    private final ProjectUpdateService projectUpdateService;

    public ProjectUpdateController(ProjectUpdateService projectUpdateService) {
        this.projectUpdateService = projectUpdateService;
    }

    @PatchMapping("/{id}")
    public ApiResponse<Void> updateProject(
            @PathVariable("id") Long id,
            @RequestBody ProjectUpdateRequest request,
            Authentication authentication
    ) {
        Long requesterId = extractUserId(authentication);
        projectUpdateService.updateProject(id, requesterId, request);
        return ApiResponse.ok(null);
    }

    private Long extractUserId(Authentication auth) {
        if (auth == null || auth.getPrincipal() == null)
            throw new ApiException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        Object p = auth.getPrincipal();
        if (p instanceof Long id) return id;
        if (p instanceof CustomUserDetails u) return u.getUserId();
        if (p instanceof String s) return Long.valueOf(s);
        throw new ApiException(HttpStatus.UNAUTHORIZED, "인증 정보가 올바르지 않습니다.");
    }
}