package com.funding.funding.domain.statistics.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "daily_statistics")
public class DailyStatistics {

    @Id
    @Column(name = "stat_date")
    private LocalDate statDate;

    @Column(name = "total_donation_amount", nullable = false)
    private Long totalDonationAmount;

    @Column(name = "project_count", nullable = false)
    private Integer projectCount;

    @Column(name = "success_project_count", nullable = false)
    private Integer successProjectCount;

    @Column(name = "user_count", nullable = false)
    private Integer userCount;

    public DailyStatistics() {}
}