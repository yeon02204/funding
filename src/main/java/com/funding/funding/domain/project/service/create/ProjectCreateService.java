package com.funding.funding.domain.project.service.create;

import org.springframework.stereotype.Service;

import com.funding.funding.domain.project.dto.ProjectCreateRequest;
import com.funding.funding.domain.project.entity.Project;
import com.funding.funding.domain.project.repository.ProjectRepository;

import jakarta.transaction.Transactional;

// 프로젝트 생성 로직 담당
// Controller가 요청을 받으면 실제 일을 하는 곳

@Service
public class ProjectCreateService { // Spring이 자동으로 Bean으로 등록해서 관리
	
	// DB에 저장하기 위한 Repository
    private final ProjectRepository projectRepository;
    
    // 생성자 주입
    public ProjectCreateService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional // 이 메서드 안의 DB작업을 하나의 작업 단위로 묶기 위함
    public Long create(ProjectCreateRequest req) { // 프로젝트 생성 로직
        Project project = new Project(); // 기본 status = DRAFT

        // 시작 예약일 세팅 (null이면 안 넣음)
        if (req.getStartAt() != null) { 
            project.scheduleStart(req.getStartAt());
        }

        if (req.getDeadline() != null) {
            project.scheduleDeadline(req.getDeadline());
        }

        // goalAmount 세팅 (Project에 setter/메서드가 있어야 함)
        project.changeGoalAmount(req.getGoalAmount());

        Project saved = projectRepository.save(project);
        return saved.getId();
    }
}