package com.funding.funding.domain.project.dto;

import java.time.LocalDateTime;

import com.funding.funding.domain.project.entity.Project;
import com.funding.funding.domain.project.status.ProjectStatus;

// 상세 응답 DTO
// API 응답 포맷을 고정해서 엔티티 변경으로 API가 굳건
// 충돌 최소 + 테스트 안정성 증가

public class ProjectDetailResponse {
    public Long id;
    public ProjectStatus status;
    public Long goalAmount;
    public LocalDateTime startAt;

    public static ProjectDetailResponse from(Project p) {
        ProjectDetailResponse r = new ProjectDetailResponse();
        r.id = p.getId();
        r.status = p.getStatus();
        r.goalAmount = p.getGoalAmount();
        r.startAt = p.getStartAt();
        return r;
    }
}