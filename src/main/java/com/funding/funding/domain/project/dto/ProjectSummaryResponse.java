package com.funding.funding.domain.project.dto;

import java.time.LocalDateTime;

import com.funding.funding.domain.project.entity.Project;
import com.funding.funding.domain.project.status.ProjectStatus;

// 목록 응답 DTO

public class ProjectSummaryResponse {
    public Long id;
    public ProjectStatus status;
    public Long goalAmount;
    public LocalDateTime startAt;

    public static ProjectSummaryResponse from(Project p) {
        ProjectSummaryResponse r = new ProjectSummaryResponse();
        r.id = p.getId();
        r.status = p.getStatus();
        r.goalAmount = p.getGoalAmount();
        r.startAt = p.getStartAt();
        return r;
    }
}