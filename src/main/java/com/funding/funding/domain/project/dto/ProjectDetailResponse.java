package com.funding.funding.domain.project.dto;

import com.funding.funding.domain.project.entity.Project;
import com.funding.funding.domain.project.entity.ProjectStatus; // ✅ entity 패키지로 통일

import java.time.LocalDateTime;

// 프로젝트 단건 조회 응답 DTO
// - 엔티티를 직접 노출하지 않고 DTO로 변환 (API 응답 포맷 고정)
public class ProjectDetailResponse {
    public Long id;
    public ProjectStatus status;
    public Long goalAmount;
    public Long currentAmount;
    public LocalDateTime startAt;
    public LocalDateTime deadline;

    public static ProjectDetailResponse from(Project p) {
        ProjectDetailResponse r = new ProjectDetailResponse();
        r.id = p.getId();
        r.status = p.getStatus();
        r.goalAmount = p.getGoalAmount();
        r.currentAmount = p.getCurrentAmount();
        r.startAt = p.getStartAt();
        r.deadline = p.getDeadline();
        return r;
    }
}