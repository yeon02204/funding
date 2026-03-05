package com.funding.funding.domain.project.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.*;

import com.funding.funding.domain.project.dto.ProjectDetailResponse;
import com.funding.funding.domain.project.dto.ProjectSummaryResponse;
import com.funding.funding.domain.project.service.query.ProjectQueryService;

// 컨트롤러는 HTTP만 담당(얇아서 충돌 적음)
// 데이터는 DTO로 고정(엔티티 변경이 API에 전염 안됨)
// 프로젝트 조회 API 담당

@RestController // http요청을 처리, 반환값을 JSON으로 바로 응답
@RequestMapping("/projects")
public class ProjectQueryController {

    private final ProjectQueryService queryService; // 조회 로직을 처리하는 서비스

    public ProjectQueryController(ProjectQueryService queryService) { // 생성자 DI
        this.queryService = queryService;
    }

    @GetMapping("/{id}")
    public ProjectDetailResponse getOne(@PathVariable Long id) {
        return ProjectDetailResponse.from(queryService.getOne(id));
    }

    @GetMapping
    public List<ProjectSummaryResponse> getAll() {
        return queryService.getAll().stream()
                .map(ProjectSummaryResponse::from)
                .collect(Collectors.toList());
    }
}