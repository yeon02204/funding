package com.funding.funding.domain.project.controller;

import com.funding.funding.domain.project.service.lifecycle.ProjectLifecycleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// 프로젝트 상태 전이 API
//
// [TODO] 인증 연동 후 아래 항목 수정 필요:
// 1. userId / adminId → Authentication에서 추출
// 2. 관리자 전용 API(approve, reject, stop, resume)에 @PreAuthorize("hasRole('ADMIN')") 추가
// 3. 사용자 전용 API(requestReview, requestDelete)에 본인 소유 프로젝트 검증 추가

@RestController
@RequestMapping("/api/projects") // ✅ /api/ prefix 통일
public class ProjectLifecycleController {

    private final ProjectLifecycleService lifecycleService;

    public ProjectLifecycleController(ProjectLifecycleService lifecycleService) {
        this.lifecycleService = lifecycleService;
    }

    // 심사 요청: DRAFT/REJECTED → REVIEW_REQUESTED
    // TODO: 인증 연동 후 userId를 Authentication에서 추출
    @PostMapping("/{id}/review-request")
    public ResponseEntity<Void> requestReview(@PathVariable Long id) {
        lifecycleService.requestReview(id, 1L); // 임시 userId=1L
        return ResponseEntity.noContent().build();
    }

    // 심사 승인: REVIEW_REQUESTED → APPROVED (관리자 전용)
    @PostMapping("/{id}/approve")
    public ResponseEntity<Void> approve(@PathVariable Long id) {
        lifecycleService.approve(id, 1L); // 임시 adminId=1L
        return ResponseEntity.noContent().build();
    }

    // 심사 반려: REVIEW_REQUESTED → REJECTED (관리자 전용)
    @PostMapping("/{id}/reject")
    public ResponseEntity<Void> reject(@PathVariable Long id) {
        lifecycleService.reject(id, 1L); // 임시 adminId=1L
        return ResponseEntity.noContent().build();
    }

    // 강제 중단: FUNDING → STOPPED (관리자 전용)
    @PostMapping("/{id}/stop")
    public ResponseEntity<Void> stop(@PathVariable Long id) {
        lifecycleService.stop(id, 1L); // 임시 adminId=1L
        return ResponseEntity.noContent().build();
    }

    // 재개: STOPPED → FUNDING (관리자 전용)
    @PostMapping("/{id}/resume")
    public ResponseEntity<Void> resume(@PathVariable Long id) {
        lifecycleService.resume(id, 1L); // 임시 adminId=1L
        return ResponseEntity.noContent().build();
    }

    // 삭제 요청: FUNDING → DELETE_REQUESTED
    @PostMapping("/{id}/delete-request")
    public ResponseEntity<Void> requestDelete(@PathVariable Long id) {
        lifecycleService.requestDelete(id, 1L); // 임시 userId=1L
        return ResponseEntity.noContent().build();
    }
}