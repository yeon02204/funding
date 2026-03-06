package com.funding.funding.domain.project.entity;

import com.funding.funding.domain.user.entity.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "project_status_logs")
public class ProjectStatusLog {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "before_status", nullable = false, length = 30)
    private String beforeStatus;

    @Column(name = "after_status", nullable = false, length = 30)
    private String afterStatus;

    @Column(name = "changed_by", nullable = false, length = 20)
    private String changedBy; // USER / ADMIN

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by_id", nullable = false)
    private User changedByUser;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public ProjectStatusLog() {}
}