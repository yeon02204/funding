package com.funding.funding.domain.project.controller;

import com.funding.funding.domain.project.dto.CreateProjectRequest;
import com.funding.funding.domain.project.service.ProjectService;
import com.funding.funding.global.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ApiResponse<Long> create(Authentication auth,
                                    @Valid @RequestBody CreateProjectRequest req) {
        return ApiResponse.ok(projectService.create(auth, req));
    }
}