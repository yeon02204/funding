package com.funding.funding.domain.project.service.update;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.funding.funding.domain.project.dto.ProjectUpdateRequest;
import com.funding.funding.domain.project.entity.Project;
import com.funding.funding.domain.project.repository.ProjectRepository;
import com.funding.funding.global.exception.ApiException;

import jakarta.transaction.Transactional;

@Service
public class ProjectUpdateService {

    private final ProjectRepository projectRepository;

    public ProjectUpdateService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional
    public void updateProject(Long projectId, Long requesterId, ProjectUpdateRequest request) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트가 존재하지 않습니다."));

        // 소유자 검증
        if (!project.getOwner().getId().equals(requesterId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "프로젝트 수정 권한이 없습니다.");
        }

        // 수정 가능 상태 검증
        project.validateEditable();

        // 실제 수정
        project.changeTitle(request.title());
        project.changeContent(request.content());
        project.changeGoalAmount(request.goalAmount());
        project.changeStartAt(request.startAt());
        project.changeDeadline(request.deadline());
    }
}