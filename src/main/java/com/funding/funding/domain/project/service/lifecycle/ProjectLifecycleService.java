package com.funding.funding.domain.project.service.lifecycle;

import com.funding.funding.domain.project.entity.Project;
import com.funding.funding.domain.project.entity.ProjectStatus;
import com.funding.funding.domain.project.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

// 프로젝트 상태 전이 전용 서비스
//
// [책임]
// - 모든 상태 변경은 이 서비스를 통해서만 처리
// - 관리자, 일반 사용자, 배치/스케줄러 요청 모두 여기서 처리
//
// [병합 수정 내역]
// - 해빈의 빈 뼈대에 성혁 로직 이식
// - changeStatus() 시그니처 변경에 맞춰 changedBy, changedById 파라미터 추가
// - 인증 연동 전까지 userId는 임시값 사용 (추후 Authentication에서 추출 예정)

@Service
public class ProjectLifecycleService {

    private final ProjectRepository projectRepository;

    public ProjectLifecycleService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    // ────────────────────────────────────────
    // 사용자 요청
    // ────────────────────────────────────────

    // 심사 요청: DRAFT / REJECTED → REVIEW_REQUESTED
    // TODO: 인증 연동 후 userId를 Authentication에서 추출할 것
    @Transactional
    public void requestReview(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId).orElseThrow();
        project.requestReview(userId);
    }

    // 삭제 요청: FUNDING → DELETE_REQUESTED
    @Transactional
    public void requestDelete(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId).orElseThrow();
        project.changeStatus(ProjectStatus.DELETE_REQUESTED, "USER", userId);
    }

    // ────────────────────────────────────────
    // 관리자 요청
    // ────────────────────────────────────────

    // 심사 승인: REVIEW_REQUESTED → APPROVED
    @Transactional
    public void approve(Long projectId, Long adminId) {
        Project project = projectRepository.findById(projectId).orElseThrow();
        project.changeStatus(ProjectStatus.APPROVED, "ADMIN", adminId);
    }

    // 심사 반려: REVIEW_REQUESTED → REJECTED
    @Transactional
    public void reject(Long projectId, Long adminId) {
        Project project = projectRepository.findById(projectId).orElseThrow();
        project.changeStatus(ProjectStatus.REJECTED, "ADMIN", adminId);
    }

    // 강제 중단: FUNDING → STOPPED
    @Transactional
    public void stop(Long projectId, Long adminId) {
        Project project = projectRepository.findById(projectId).orElseThrow();
        project.changeStatus(ProjectStatus.STOPPED, "ADMIN", adminId);
    }

    // 재개 승인: STOPPED → FUNDING
    @Transactional
    public void resume(Long projectId, Long adminId) {
        Project project = projectRepository.findById(projectId).orElseThrow();
        project.changeStatus(ProjectStatus.FUNDING, "ADMIN", adminId);
    }

    // 삭제 완료: DELETE_REQUESTED → DELETED (환불 완료 후 시스템 호출)
    @Transactional
    public void completeDelete(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow();
        project.changeStatus(ProjectStatus.DELETED, "SYSTEM", 0L);
    }

    // ────────────────────────────────────────
    // 배치 / 스케줄러
    // ────────────────────────────────────────

    // APPROVED & startAt <= now 인 프로젝트를 FUNDING으로 전환
    @Transactional
    public void transitionApprovedToFunding(LocalDateTime now) {
        List<Project> targets =
                projectRepository.findByStatusAndStartAtLessThanEqual(ProjectStatus.APPROVED, now);
        for (Project project : targets) {
            project.startFunding();
        }
    }

    // FUNDING & deadline <= now 인 프로젝트를 SUCCESS / FAILED로 전환
    @Transactional
    public int completeFundingByDeadline(LocalDateTime now) {
        List<Project> targets =
                projectRepository.findByStatusAndDeadlineLessThanEqual(ProjectStatus.FUNDING, now);
        for (Project project : targets) {
            project.completeFunding();
        }
        return targets.size();
    }
}