package com.funding.funding.domain.project.dto;

import com.funding.funding.domain.project.entity.Project;
import com.funding.funding.domain.project.entity.ProjectStatus;

import java.time.LocalDateTime;
import java.util.List;

public class ProjectDetailResponse {

    public Long id;
    public String title;
    public String content;
    public String categoryName;
    public Long categoryId;
    public String ownerNickname;
    public Long ownerId;
    public ProjectStatus status;
    public Long goalAmount;
    public Long currentAmount;
    public int progressPercent;
    public long likeCount;
    public LocalDateTime startAt;
    public LocalDateTime deadline;
    public LocalDateTime createdAt;

    // ✅ 이미지
    public String thumbnailUrl;       // 대표 이미지
    public List<String> imageUrls;    // 전체 이미지 목록

    public static ProjectDetailResponse from(Project p, long likeCount) {

        ProjectDetailResponse r = new ProjectDetailResponse();

        r.id            = p.getId();
        r.title         = p.getTitle();
        r.content       = p.getContent();
        r.categoryName  = p.getCategory() != null ? p.getCategory().getName() : null;
        r.categoryId    = p.getCategory() != null ? p.getCategory().getId()   : null;
        r.ownerNickname = p.getOwner()    != null ? p.getOwner().getNickname(): null;
        r.ownerId       = p.getOwner()    != null ? p.getOwner().getId()      : null;
        r.status        = p.getStatus();
        r.goalAmount    = p.getGoalAmount();
        r.currentAmount = p.getCurrentAmount();
        r.progressPercent = (p.getGoalAmount() != null && p.getGoalAmount() > 0)
                ? (int) (p.getCurrentAmount() * 100L / p.getGoalAmount())
                : 0;
        r.likeCount   = likeCount;
        r.startAt     = p.getStartAt();
        r.deadline    = p.getDeadline();
        r.createdAt   = p.getCreatedAt();

        return r;
    }

    public static ProjectDetailResponse from(Project p) {
        return from(p, 0L);
    }
}