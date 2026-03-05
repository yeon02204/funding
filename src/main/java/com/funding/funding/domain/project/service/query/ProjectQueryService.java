package com.funding.funding.domain.project.service.query;

import java.util.List;

import org.springframework.stereotype.Service;

import com.funding.funding.domain.project.entity.Project;
import com.funding.funding.domain.project.repository.ProjectRepository;

import jakarta.transaction.Transactional;

// 실제 DB 조회 로직 담당(Repository 호출 담당)
// 컨트롤러는 요청/응답만 하도록 얇게 유지

@Service // 비즈니스 로직을 담당
public class ProjectQueryService {

    private final ProjectRepository projectRepository; // DB 조회를 위한 Repository

    public ProjectQueryService(ProjectRepository projectRepository) { // 생성자 주입
        this.projectRepository = projectRepository;
    }

    @Transactional
    public Project getOne(Long id) { // 특정 프로젝트 하나 조회, 요청이 들어오면 Controller가 이 메서드 호출
        return projectRepository.findById(id).orElseThrow();
    }

    @Transactional
    public List<Project> getAll() { // 모든 프로젝트 조회, 요청이 들어오면 이 Controller가 이 메서드 호출
        return projectRepository.findAll();
    }
}