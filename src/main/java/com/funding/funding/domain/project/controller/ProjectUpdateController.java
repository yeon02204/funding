package com.funding.funding.domain.project.controller;

import com.funding.funding.domain.project.dto.ProjectUpdateRequest;
import com.funding.funding.domain.project.service.update.ProjectUpdateService;
import com.funding.funding.global.response.ApiResponse;
import com.funding.funding.global.security.CustomUserDetails;
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
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long requesterId = userDetails.getUserId();

        projectUpdateService.updateProject(id, requesterId, request);

        return ApiResponse.ok(null);
    }
}