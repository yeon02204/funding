package com.funding.funding.domain.project.dto;

import com.funding.funding.domain.project.entity.Project;
import com.funding.funding.domain.project.entity.ProjectStatus; // ✅ entity 패키지로 통일

import java.time.LocalDateTime;

// 프로젝트 목록 조회 응답 DTO
public class ProjectSummaryResponse {
    public Long id;
    public ProjectStatus status;
    public Long goalAmount;
    public Long currentAmount;
    public LocalDateTime startAt;
    public LocalDateTime deadline;

    public static ProjectSummaryResponse from(Project p) {
        ProjectSummaryResponse r = new ProjectSummaryResponse();
        r.id = p.getId();
        r.status = p.getStatus();
        r.goalAmount = p.getGoalAmount();
        r.currentAmount = p.getCurrentAmount();
        r.startAt = p.getStartAt();
        r.deadline = p.getDeadline();
        return r;
    }
}