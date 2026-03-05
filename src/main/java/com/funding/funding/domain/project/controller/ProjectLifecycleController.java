package com.funding.funding.domain.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.funding.funding.domain.project.service.lifecycle.ProjectLifecycleService;

@RestController // API 서버 역할 담당
@RequestMapping("/projects")
public class ProjectLifecycleController {

    private final ProjectLifecycleService lifecycleService; // 컨트롤러 -> 서비스 연결

    public ProjectLifecycleController(ProjectLifecycleService lifecycleService) {
        this.lifecycleService = lifecycleService;
    }

    // 심사 요청: DRAFT/REJECTED -> REVIEW_REQUESTED
    @PostMapping("/{id}/review-request") // Post 요청 -> 데이터 변경
    public ResponseEntity<Void> requestReview(@PathVariable Long id) { // URL에서 ID를 가져옴
        lifecycleService.requestReview(id);
        return ResponseEntity.noContent().build(); // 응답을 보내는 것
    }

    // 승인: REVIEW_REQUESTED -> APPROVED
    @PostMapping("/{id}/approve") 
    public ResponseEntity<Void> approve(@PathVariable Long id) { 
        lifecycleService.approve(id);
        return ResponseEntity.noContent().build();
    }

    // 반려: REVIEW_REQUESTED -> REJECTED
    @PostMapping("/{id}/reject") 
    public ResponseEntity<Void> reject(@PathVariable Long id) {
        lifecycleService.reject(id);
        return ResponseEntity.noContent().build();
    }

    // 중지: FUNDING -> STOPPED
    @PostMapping("/{id}/stop")
    public ResponseEntity<Void> stop(@PathVariable Long id) {
        lifecycleService.stop(id);
        return ResponseEntity.noContent().build();
    }

    // 재개: STOPPED -> FUNDING
    @PostMapping("/{id}/resume")
    public ResponseEntity<Void> resume(@PathVariable Long id) {
        lifecycleService.resume(id);
        return ResponseEntity.noContent().build();
    }

    // 삭제 요청: FUNDING -> DELETE_REQUESTED
    @PostMapping("/{id}/delete-request")
    public ResponseEntity<Void> requestDelete(@PathVariable Long id) {
        lifecycleService.requestDelete(id);
        return ResponseEntity.noContent().build();
    }
}
