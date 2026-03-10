package com.funding.funding.domain.project.dto;

import com.funding.funding.domain.project.entity.Project;
import com.funding.funding.domain.project.entity.ProjectStatus;

import java.time.LocalDateTime;

// 프로젝트 목록 조회 응답 DTO
// - 목록에서 보여줄 핵심 정보만 담는다 (상세는 ProjectDetailResponse)
public class ProjectSummaryResponse {

    public Long id;
    public String title;           // ✅ 추가 — 목록에 제목 없으면 안 됨
    public String categoryName;    // ✅ 추가 — 필터링 결과 확인용
    public ProjectStatus status;
    public Long goalAmount;
    public Long currentAmount;
    public int progressPercent;    // ✅ 추가 — 달성률 (currentAmount / goalAmount * 100)
    public LocalDateTime startAt;
    public LocalDateTime deadline;

    public static ProjectSummaryResponse from(Project p) {
        ProjectSummaryResponse r = new ProjectSummaryResponse();
        r.id            = p.getId();
        r.title         = p.getTitle();
        r.categoryName  = p.getCategory() != null ? p.getCategory().getName() : null;
        r.status        = p.getStatus();
        r.goalAmount    = p.getGoalAmount();
        r.currentAmount = p.getCurrentAmount();
        r.progressPercent = (p.getGoalAmount() != null && p.getGoalAmount() > 0)
                ? (int) (p.getCurrentAmount() * 100L / p.getGoalAmount())
                : 0;
        r.startAt    = p.getStartAt();
        r.deadline   = p.getDeadline();
        return r;
    }
}