package com.funding.funding.domain.project.scheduler;

import com.funding.funding.domain.project.service.lifecycle.ProjectLifecycleService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

// 프로젝트 상태 자동 전환 스케줄러
// 5분마다 실행:
// 1. APPROVED & startAt <= now  → FUNDING 전환
// 2. FUNDING  & deadline <= now → SUCCESS / FAILED 전환
@Component
public class ProjectFundingScheduler {

    private final ProjectLifecycleService service;

    public ProjectFundingScheduler(ProjectLifecycleService service) {
        this.service = service;
    }

    @Scheduled(fixedDelay = 300_000) // 5분마다
    public void run() {
        LocalDateTime now = LocalDateTime.now();

        // 1단계: 시작일 도달한 APPROVED 프로젝트 → FUNDING
        service.transitionApprovedToFunding(now);

        // 2단계: 마감일 지난 FUNDING 프로젝트 → SUCCESS 또는 FAILED
        service.completeFundingByDeadline(now);
    }
}