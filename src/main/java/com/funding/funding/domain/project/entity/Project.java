package com.funding.funding.domain.project.entity;

import com.funding.funding.domain.category.entity.Category;
import com.funding.funding.domain.user.entity.User;
import com.funding.funding.global.util.BaseTimeEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "projects")
public class Project extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 255)
    private String title;

    @Lob
    private String content;

    @Column(name = "goal_amount", nullable = false)
    private Long goalAmount;

    @Column(name = "current_amount", nullable = false)
    private Long currentAmount = 0L;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProjectStatus status;

    @Column(name = "start_at")
    private LocalDateTime startAt;

    @Column(nullable = false)
    private LocalDateTime deadline;

    @Version
    private Long version; // ✅ nullable=false 제거 권장 (JPA가 관리)

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    protected Project() {}

    
    @PrePersist
    void prePersist() {
        if (status == null) status = ProjectStatus.DRAFT;
        if (currentAmount == null) currentAmount = 0L;
    }
    
    
    
    // ✅ 생성용 팩토리 (추천)
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

        p.status = ProjectStatus.DRAFT;  // ✅ 기본값
        p.currentAmount = 0L;            // ✅ 기본값
        p.startAt = null;                // 시작 전이니 null

        return p;
    }

    // ✅ 최소 getter (응답/조회 때 필요)
    public Long getId() { return id; }
    public User getOwner() { return owner; }
    public Category getCategory() { return category; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public Long getGoalAmount() { return goalAmount; }
    public Long getCurrentAmount() { return currentAmount; }
    public ProjectStatus getStatus() { return status; }
    public LocalDateTime getDeadline() { return deadline; }
}