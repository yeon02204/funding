package com.funding.funding.domain.project.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.funding.funding.domain.project.exception.InvalidProjectStatusTransitionException;
import com.funding.funding.domain.project.status.ProjectStatus;
import com.funding.funding.domain.project.status.ProjectStatusPolicy;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

/**
 * [역할]
 * - 프로젝트 정보를 저장하는 엔티티(현재는 최소 구현)
 *
 * [현재 목표]
 * - 상태 전이 규칙은 ProjectStatusPolicy로 검증하고
 * - 상태 변경이 성공하면 ProjectStatusLog를 1개 남긴다
 * - APPROVED 상태에서 start_at 도달 시 FUNDING으로 전환될 수 있도록 startAt(예약 시작일)을 가진다
 */

// 로그를 만드는 곳
@Entity
public class Project {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
    @Enumerated(EnumType.STRING)
    private ProjectStatus status = ProjectStatus.DRAFT; // 프로젝트는 현재상태를 하나 갖고 있고 기본 : DRAFT

    // ✅ 펀딩 시작 예약일 (DB: start_at)
    @Column(name = "start_at")
    private LocalDateTime startAt; // ProjectFundingTransitionService랑 연결, 
    							   // 승인 해놓고 시작 시간 되면 자동으로 펀딩 시작 가능 장치
    @Column(name = "deadline")
    private LocalDateTime deadline; // 서버 돌리다가 오류 새로 추가
    
    // goal_Amount 필드 매핑
    @Column(name = "goal_amount")
    private Long goalAmount;

    // ✅ 상태 변경 로그 목록 (상태가 바뀔 때마다 1개씩 쌓임)
    // DB 관점에서도 이 로그들은 이 프로젝트에 속한다가 명확해짐
    // Project를 저장할 때 로그도 같이 저장되게 만들기 가능
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ProjectStatusLog> statusLogs = new ArrayList<>();

    // ✅ 공통 상태 변경 메서드 (상태 전이의 유일한 관문)
    // 하는 일 : 정책검사 - 상태 전이가 허용되는지 규칙으로 검사, 허용 안되면 예외 발생
    public void changeStatus(ProjectStatus nextStatus) { 
        ProjectStatus from = this.status;

        if (!ProjectStatusPolicy.isAllowed(this.status, nextStatus)) {
            throw new InvalidProjectStatusTransitionException(this.status, nextStatus);
            // 현재 요청은 규칙 위반이라고 강하게 막는 역할
        }

        this.status = nextStatus; // 상태 변경

        // ✅ 상태 변경 성공 후 로그 기록 - 상태 변경 시 무조건 로그 1개 생성
        statusLogs.add(new ProjectStatusLog( 
                this,               // 로그가 DB에 저장될 때 project_id가 자동으로 채워짐
                from,               // beforeStatus
                nextStatus,         // afterStatus
                "USER",             // changedBy (임시)
                0L,                 // changedById (임시)
                LocalDateTime.now() // createdAt
        ));
    }

    // ✅ 심사 요청 행위 메서드
    public void requestReview() {
        changeStatus(ProjectStatus.REVIEW_REQUESTED); // 상태 전이 정책 검사, 상태 변경, 상태 로그 생성
    }

    // ✅ 펀딩 시작 예약일 설정 (승인 이후 “예약 시작”을 위해 사용)
    public void scheduleStart(LocalDateTime startAt) {
        if (startAt == null) {
            throw new IllegalArgumentException("startAt cannot be null");
        }
        this.startAt = startAt;
    }
    
    // 펀딩 종료
    public void scheduleDeadline(LocalDateTime deadline) {
        if (deadline == null) {
            throw new IllegalArgumentException("deadline cannot be null");
        }
        this.deadline = deadline;
    }

    // ✅ 예약 시작일 도달 시 펀딩 시작 전환
    public void startFunding() {
        changeStatus(ProjectStatus.FUNDING);
    }
    
    public void completeFunding(boolean success) { // 종료 처리 메서드 추가
        changeStatus(success ? ProjectStatus.SUCCESS : ProjectStatus.FAILED);
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }
    
    public LocalDateTime getDeadline() { // 서버 돌리다가 오류 새로 추가
        return deadline;
    }

    public List<ProjectStatusLog> getStatusLogs() {
        return statusLogs;
    }
    
    public Long getGoalAmount() { // getter 추가
        return goalAmount;
    }
    
    public void setGoalAmount(Long goalAmount) {
        this.goalAmount = goalAmount;
    }
    
    public Long getId() {
        return id;
    }
    
    // ✅ 프로젝트 수정 가능 여부 검증
    public void validateEditable() { // 수정 제한 정책의 핵심
        if (this.status != ProjectStatus.DRAFT) {
            throw new IllegalStateException("프로젝트는 초안 상태에서만 수정할 수 있습니다.");
        }
    }
    
    public void changeGoalAmount(Long goalAmount) {
        if (goalAmount == null || goalAmount <= 0) {
            throw new IllegalArgumentException("goalAmount must be positive");
        }
        this.goalAmount = goalAmount;
    }
}