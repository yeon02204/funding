package com.funding.funding.domain.project.controller;

import com.funding.funding.domain.project.dto.ProjectDetailResponse;
import com.funding.funding.domain.project.dto.ProjectSummaryResponse;
import com.funding.funding.domain.project.service.query.ProjectQueryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

// 프로젝트 조회 API
@RestController
@RequestMapping("/api/projects") // ✅ /api/ prefix 통일
public class ProjectQueryController {

    private final ProjectQueryService queryService;

    public ProjectQueryController(ProjectQueryService queryService) {
        this.queryService = queryService;
    }

    // 단건 조회
    @GetMapping("/{id}")
    public ProjectDetailResponse getOne(@PathVariable Long id) {
        return ProjectDetailResponse.from(queryService.getOne(id));
    }

    // 전체 목록 조회
    @GetMapping
    public List<ProjectSummaryResponse> getAll() {
        return queryService.getAll().stream()
                .map(ProjectSummaryResponse::from)
                .collect(Collectors.toList());
    }
}