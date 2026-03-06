package com.funding.funding.domain.donation.entity;

import com.funding.funding.domain.project.entity.Project;
import com.funding.funding.domain.user.entity.User;
import com.funding.funding.global.util.BaseTimeEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "donations")
public class Donation extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DonationStatus status;

    @Column(name = "cancel_deadline")
    private LocalDateTime cancelDeadline;

    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;

    @Version
    @Column(nullable = false)
    private Long version;

    public Donation() {}
}