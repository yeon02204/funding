package com.funding.funding.domain.project.entity;

import com.funding.funding.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

// 프로젝트 상태 변경 로그 엔티티
//
// [설계 결정]
// - 로그는 한 번 생성되면 수정 불가 → setter 없음, 생성자로만 값 세팅
// - changedBy: "USER" / "ADMIN" / "SYSTEM" (String 유지 - 나중에 enum 전환 가능)
// - changedByUser: 해빈 엔티티 구조 기반 @ManyToOne 방식
// - before/afterStatus: enum 타입 유지 (성혁 방식 - 타입 안전성 보장)
//
// [병합 수정 내역]
// - 성혁: changedById(Long) → 해빈: @ManyToOne User changedByUser 방식으로 통합
// - before/afterStatus는 성혁의 enum 방식 유지 (해빈의 String보다 타입 안전)

@Getter
@Entity
@Table(name = "project_status_logs")
public class ProjectStatusLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 프로젝트의 상태가 변경됐는지
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    // 변경 전 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "before_status", nullable = false, length = 30)
    private ProjectStatus beforeStatus;

    // 변경 후 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "after_status", nullable = false, length = 30)
    private ProjectStatus afterStatus;

    // 변경 주체 구분: "USER" / "ADMIN" / "SYSTEM"
    @Column(name = "changed_by", nullable = false, length = 20)
    private String changedBy;

    // 변경 주체 User 객체 (@ManyToOne - 해빈 구조 기준)
    // SYSTEM 자동 전환 시에는 null 허용
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by_id")
    private User changedByUser;

    // 상태 변경이 일어난 시간
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // JPA 기본 생성자 (외부 사용 X)
    protected ProjectStatusLog() {}

    // 일반 유저/관리자 상태 변경 시 사용 (User 객체 있을 때)
    public ProjectStatusLog(
            Project project,
            ProjectStatus beforeStatus,
            ProjectStatus afterStatus,
            String changedBy,
            User changedByUser,
            LocalDateTime createdAt
    ) {
        this.project = project;
        this.beforeStatus = beforeStatus;
        this.afterStatus = afterStatus;
        this.changedBy = changedBy;
        this.changedByUser = changedByUser;
        this.createdAt = createdAt;
    }

    // 시스템 자동 전환 시 사용 (스케줄러 등 - User 없음)
    public ProjectStatusLog(
            Project project,
            ProjectStatus beforeStatus,
            ProjectStatus afterStatus,
            String changedBy,
            LocalDateTime createdAt
    ) {
        this.project = project;
        this.beforeStatus = beforeStatus;
        this.afterStatus = afterStatus;
        this.changedBy = changedBy;
        this.changedByUser = null;
        this.createdAt = createdAt;
    }

}