package com.funding.donation.entity;

import com.funding.project.entity.Project;
import com.funding.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "donations")
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 후원자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 프로젝트
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    // 금액
    @Column(nullable = false)
    private Long amount;

    // 상태
    @Column(nullable = false, length = 20)
    private String status;

    // 취소 마감
    private LocalDateTime cancelDeadline;

    // 환불 시간
    private LocalDateTime refundedAt;

    @Version
    private Long version;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ===== 기본 생성자 =====
    protected Donation() {}

    // ===== 생성자 (donate용) =====
    public Donation(User user, Project project, Long amount, String status,
                    LocalDateTime cancelDeadline,
                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.user = user;
        this.project = project;
        this.amount = amount;
        this.status = status;
        this.cancelDeadline = cancelDeadline;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ===== Getter =====
    public Long getId() { return id; }
    public User getUser() { return user; }
    public Project getProject() { return project; }
    public Long getAmount() { return amount; }
    public String getStatus() { return status; }
    public LocalDateTime getCancelDeadline() { return cancelDeadline; }
    public LocalDateTime getRefundedAt() { return refundedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // ===== Setter (서비스에서 쓰는 것만) =====
    public void setStatus(String status) {
        this.status = status;
    }

    public void setRefundedAt(LocalDateTime refundedAt) {
        this.refundedAt = refundedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}