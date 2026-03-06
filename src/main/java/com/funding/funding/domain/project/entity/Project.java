package com.funding.funding.domain.project.entity;

import com.funding.funding.domain.category.entity.Category;
import com.funding.funding.domain.project.exception.InvalidProjectStatusTransitionException; // ✅ import 추가
import com.funding.funding.domain.project.status.ProjectStatusPolicy;                        // ✅ import 추가
import com.funding.funding.domain.user.entity.User;
import com.funding.funding.global.util.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
public class Project extends BaseTimeEntity {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Getter
    @Column(nullable = false, length = 255)
    private String title;

    @Getter
    @Lob
    private String content;

    @Getter
    @Column(name = "goal_amount", nullable = false)
    private Long goalAmount;

    // 현재 모인 금액 (팀 합의로 추가된 컬럼)
    @Getter
    @Column(name = "current_amount", nullable = false)
    private Long currentAmount = 0L;

    // 상태 전이는 changeStatus()로만 가능
    @Getter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProjectStatus status;

    @Getter
    @Column(name = "start_at")
    private LocalDateTime startAt;

    @Getter
    @Column(nullable = false)
    private LocalDateTime deadline;

    @Getter
    @Version
    private Long version;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ProjectStatusLog> statusLogs = new ArrayList<>();

    public Project() {}

    // ✅ @PrePersist 제거
    // BaseTimeEntity에 이미 @PrePersist(onCreate)가 있어서 중복 충돌 발생
    // status 초기값은 create() 팩토리 메서드에서 DRAFT로 세팅
    // currentAmount 초기값은 필드 선언부에서 0L로 세팅

    // ────────────────────────────────────────
    // 팩토리 메서드
    // ────────────────────────────────────────

    public static Project create(User owner, Category category,
                                 String title, String content,
                                 Long goalAmount, LocalDateTime deadline) {
        Project p = new Project();
        p.owner = owner;
        p.category = category;
        p.title = title;
        p.content = content;
        p.goalAmount = goalAmount;
        p.deadline = deadline;
        p.status = ProjectStatus.DRAFT;
        p.currentAmount = 0L;
        return p;
    }

    // ────────────────────────────────────────
    // 상태 전이 (단일 관문)
    // ────────────────────────────────────────

    // TODO: 인증 연동 후 changedBy, changedById를 Authentication에서 추출할 것
    public void changeStatus(ProjectStatus nextStatus, String changedBy, Long changedById) {
        ProjectStatus from = this.status;

        if (!ProjectStatusPolicy.isAllowed(this.status, nextStatus)) {
            throw new InvalidProjectStatusTransitionException(this.status, nextStatus);
        }

        this.status = nextStatus;

        // changedById == 0L → SYSTEM 자동 전환, User 없는 생성자 사용
        if (changedById == null || changedById == 0L) {
            statusLogs.add(new ProjectStatusLog(
                    this, from, nextStatus, changedBy, LocalDateTime.now()
            ));
        } else {
            // TODO: 인증 연동 후 User 객체 직접 넘기도록 개선
            statusLogs.add(new ProjectStatusLog(
                    this, from, nextStatus, changedBy, LocalDateTime.now()
            ));
        }
    }

    // ────────────────────────────────────────
    // 행위 메서드
    // ────────────────────────────────────────

    public void requestReview(Long userId) {
        changeStatus(ProjectStatus.REVIEW_REQUESTED, "USER", userId);
    }

    public void scheduleStart(LocalDateTime startAt) {
        if (startAt == null) throw new IllegalArgumentException("startAt cannot be null");
        this.startAt = startAt;
    }

    public void scheduleDeadline(LocalDateTime deadline) {
        if (deadline == null) throw new IllegalArgumentException("deadline cannot be null");
        this.deadline = deadline;
    }

    public void startFunding() {
        changeStatus(ProjectStatus.FUNDING, "SYSTEM", 0L);
    }

    public void completeFunding() {
        if (this.goalAmount == null) throw new IllegalStateException("goalAmount must be set");
        long current = this.currentAmount == null ? 0L : this.currentAmount;
        changeStatus(current >= this.goalAmount ? ProjectStatus.SUCCESS : ProjectStatus.FAILED, "SYSTEM", 0L);
    }

    public void validateEditable() {
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

    public void increaseCurrentAmount(long amount) {
        if (amount <= 0) throw new IllegalArgumentException("amount must be positive");
        if (this.currentAmount == null) this.currentAmount = 0L;
        this.currentAmount += amount;
    }

    public void decreaseCurrentAmount(long amount) {
        if (amount <= 0) throw new IllegalArgumentException("amount must be positive");
        if (this.currentAmount == null) this.currentAmount = 0L;
        if (this.currentAmount < amount) throw new IllegalStateException("currentAmount cannot be negative");
        this.currentAmount -= amount;
    }

    // ────────────────────────────────────────
    // Getter
    // ────────────────────────────────────────

}