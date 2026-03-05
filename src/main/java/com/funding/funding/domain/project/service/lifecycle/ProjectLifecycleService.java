package com.funding.funding.domain.project.service.lifecycle;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.funding.funding.domain.project.entity.Project;
import com.funding.funding.domain.project.repository.ProjectRepository;
import com.funding.funding.domain.project.status.ProjectStatus;

import jakarta.transaction.Transactional;

// 프로젝트 상태를 바꾸는 유일한 통로
// 

@Service
public class ProjectLifecycleService {
	
	// DB에서 Project를 가져오기 위한 도구
	// DB조회 -> Entity 호출 -> 상태 변경
    private final ProjectRepository projectRepository;

    public ProjectLifecycleService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    /** 사용자 요청: 심사 요청 */
    @Transactional
    public void requestReview(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(); // DB에서 프로젝트 가져옴
        project.requestReview(); // 내부에서 policy + log
    }
    
    @Transactional
    public void approve(Long projectId) { // 심사 통과 처리 REVIEW_REQUESTED → APPROVED 변경
        Project project = projectRepository.findById(projectId).orElseThrow();
        project.changeStatus(ProjectStatus.APPROVED);
    }

    @Transactional
    public void reject(Long projectId) { // 심사 반려 처리
        Project project = projectRepository.findById(projectId).orElseThrow();
        project.changeStatus(ProjectStatus.REJECTED);
    }

    @Transactional
    public void stop(Long projectId) { // 펀딩 중단 
        Project project = projectRepository.findById(projectId).orElseThrow();
        project.changeStatus(ProjectStatus.STOPPED);
    }

    @Transactional
    public void resume(Long projectId) { // 중단된 펀딩 재개
        Project project = projectRepository.findById(projectId).orElseThrow();
        project.changeStatus(ProjectStatus.FUNDING);
    }

    @Transactional
    public void requestDelete(Long projectId) { // 삭제 요청 
        Project project = projectRepository.findById(projectId).orElseThrow();
        project.changeStatus(ProjectStatus.DELETE_REQUESTED);
    }

    /** 배치/스케줄: APPROVED & startAt<=now 인 것들을 FUNDING으로 전환 */
    @Transactional
    public int transitionApprovedToFunding(LocalDateTime now) { // 승인된 프로젝트 중 시작시간이 된 것들을 자동으로 펀딩 시작
        List<Project> targets =
            projectRepository.findByStatusAndStartAtLessThanEqual(ProjectStatus.APPROVED, now);

        for (Project project : targets) { 
            project.startFunding(); // 내부에서 policy + log
        }
        return targets.size();
    }
}