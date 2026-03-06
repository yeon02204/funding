package com.funding.funding.domain.donation.entity;

import com.funding.funding.domain.project.entity.Project;
import com.funding.funding.domain.user.entity.User;
import com.funding.funding.domain.donation.status.DonationStatus; // ✅ 상태 전이 로직(canTransitionTo)이 있는 정연 DonationStatus 사용
import com.funding.funding.global.util.BaseTimeEntity;           // ✅ createdAt / updatedAt 자동 관리 (해빈 BaseTimeEntity)
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// 후원 도메인 엔티티 (상태 기반 후원 관리)
//
// [설계 결정]
// - User, Project는 Long ID 대신 @ManyToOne으로 참조 (JPA 정석, Fetch Join 최적화 가능)
// - createdAt / updatedAt은 BaseTimeEntity가 자동 관리 (수동 세팅 실수 방지)
// - DonationStatus는 canTransitionTo() 전이 로직이 포함된 정연 버전 사용
// - @Version으로 Optimistic Lock 적용 (동시 후원 충돌 방지)

@Getter
@Entity
@Table(name = "donations")
public class Donation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 후원한 사용자 (users 테이블 FK)
    // LAZY: 후원 조회 시 User 정보가 항상 필요하지 않으므로 지연 로딩
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 후원 대상 프로젝트 (projects 테이블 FK)
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    // 후원 금액 (최소 1,000원 / 1,000원 단위 - 서비스 레이어에서 검증)
    @Setter
    @Column(nullable = false)
    private Long amount;

    // 후원 상태 (PENDING / SUCCESS / FAILED / CANCEL / REFUND)
    // 상태 전이는 DonationStatus.canTransitionTo()로 검증
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DonationStatus status;

    // 취소 가능 마감 시간 (결제 완료 기준 24시간)
    // 이 시간 이후에는 CANCEL 불가
    @Setter
    @Column(name = "cancel_deadline")
    private LocalDateTime cancelDeadline;

    // 환불 처리 시간 기록 (REFUND 상태 전환 시 세팅)
    @Setter
    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;

    // Optimistic Lock - 동시에 같은 후원 건을 수정할 때 충돌 감지
    @Version
    @Column(nullable = false)
    private Long version;

    // JPA 기본 생성자 (외부에서 직접 사용 X)
    public Donation() {}

    // ────────────────────────────────────────
    // Getter
    // ────────────────────────────────────────

    // ────────────────────────────────────────
    // Setter (상태 변경은 서비스 레이어에서만 호출)
    // ────────────────────────────────────────

}
