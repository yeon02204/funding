package com.funding.project.controller;

import com.funding.project.entity.Project;
import com.funding.project.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    // ✅ 프로젝트 단건 조회
    @GetMapping("/{projectId}")
    public ResponseEntity<Project> getProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.getProject(projectId));
    }

    // ✅ 전체 프로젝트 조회
    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    // ✅ 상태별 프로젝트 조회
    // /projects/status/FUNDING
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Project>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(projectService.getProjectsByStatus(status));
    }

    // ✅ 특정 유저 프로젝트 조회
    // /projects/user/1
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Project>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(projectService.getProjectsByUser(userId));
    }
}