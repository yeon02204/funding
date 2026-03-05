package com.funding.project.service;

import com.funding.project.entity.Project;
import com.funding.project.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    // ✅ 프로젝트 단건 조회
    public Project getProject(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트 없음"));
    }

    // ✅ 전체 프로젝트 조회
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    // ✅ 상태별 프로젝트 조회 (FUNDING, SUCCESS 등)
    public List<Project> getProjectsByStatus(String status) {
        return projectRepository.findByStatus(status);
    }

    // ✅ 특정 유저가 만든 프로젝트 조회
    public List<Project> getProjectsByUser(Long userId) {
        return projectRepository.findByUserId(userId);
    }
}