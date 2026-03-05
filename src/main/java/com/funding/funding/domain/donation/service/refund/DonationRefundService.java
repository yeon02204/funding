package com.funding.funding.domain.donation.service.refund;

import com.funding.funding.domain.donation.entity.Donation;
import com.funding.funding.domain.donation.repository.DonationRepository;
import com.funding.funding.domain.donation.status.DonationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/*
 * [역할]
 * 후원 환불 전용 서비스
 *
 * [책임]
 * SUCCESS → REFUND 전이
 * 환불 처리 시간 기록
 */
@Service
@RequiredArgsConstructor
public class DonationRefundService {

    private final DonationRepository donationRepository;

    // Logger 선언
    private static final Logger log =
            LoggerFactory.getLogger(DonationRefundService.class);

    @Transactional
    public void refund(Long donationId) {

        // 환불 요청 들어왔다는 로그
        log.info("[REFUND] request donationId={}", donationId);

        // 1. 후원 조회
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new IllegalArgumentException("Donation not found"));

        // 조회 직후 현재 상태 로그
        log.info("[REFUND] current status={}, refundedAt={}",
                donation.getStatus(),
                donation.getRefundedAt());

        // 이미 환불된 경우 방어
        if (donation.getRefundedAt() != null) {
            log.warn("[REFUND] already refunded donationId={}", donationId);
            throw new IllegalStateException("Already refunded");
        }

        // 2. 상태 전이 가능 여부 검증
        if (!donation.getStatus().canTransitionTo(DonationStatus.REFUND)) {

            // 상태 전이 불가 시 warn
            log.warn("[REFUND] invalid transition. donationId={}, status={}",
                    donation.getId(),
                    donation.getStatus());
            throw new IllegalStateException("Invalid state transition");
        }


        DonationStatus before = donation.getStatus();

        // 3. 상태 변경
        donation.setStatus(DonationStatus.REFUND);
        donation.setRefundedAt(LocalDateTime.now());

        // 상태 변경 로그
        log.info("[REFUND] status changed: {} -> {} (donationId={})",
                before,
                DonationStatus.REFUND,
                donation.getId());

        // 4. 환불 처리 시간 기록
        donation.setRefundedAt(LocalDateTime.now());
    }
}