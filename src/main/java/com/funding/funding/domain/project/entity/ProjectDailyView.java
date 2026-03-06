package com.funding.funding.domain.project.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "project_daily_views",
       uniqueConstraints = @UniqueConstraint(name = "uq_pdv_project_date", columnNames = {"project_id", "view_date"}))
public class ProjectDailyView {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "view_date", nullable = false)
    private LocalDate viewDate;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount;

    public ProjectDailyView() {}
}