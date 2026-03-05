package com.funding.funding.domain.donation.service.cancel;

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
 * 사용자 취소(개인 환불) 전용 서비스
 * - 실제 돈이 오가는것이 아니라서 취소 상태만 가진다.
 * - 결제 완료 상태를 되돌리는 도메인 상태 변경 로직이다.
 *
 * [책임]
 * SUCCESS → CANCEL 상태 전이
 * 24시간 이내만 허용
 */
@Service
@RequiredArgsConstructor
public class DonationCancelService {

    private final DonationRepository donationRepository;

    // Logger 선언
    private static final Logger log =
            LoggerFactory.getLogger(DonationCancelService.class);

    @Transactional
    public void cancel(Long donationId) {

        log.info("[CANCEL] request donationId={}", donationId);

        // 1. 후원 조회
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new IllegalArgumentException("Donation not found"));

        // 조회 직후 현재 상태 로그
        log.info("[CANCEL] current status={}, deadline={}",
                donation.getStatus(),
                donation.getCancelDeadline());

        // 2. 상태 전이 가능 여부 확인 (SUCCESS → CANCEL)
        if (!donation.getStatus().canTransitionTo(DonationStatus.CANCEL)) {
            // 상태 전이 불가일 때 warn
            log.warn("[CANCEL] invalid transition. donationId={}, status={}",
                    donation.getId(),
                    donation.getStatus());
            throw new IllegalStateException("Invalid state transition");
        }

        // 3. 취소 가능 시간 검증
        if (LocalDateTime.now().isAfter(donation.getCancelDeadline())) {
            // 마감 시간 초과 시 warn
            log.warn("[CANCEL] deadline exceeded. donationId={}, deadline={}",
                    donation.getId(),
                    donation.getCancelDeadline());
            throw new IllegalStateException("Cancel deadline exceeded");
        }

        // 4. 상태 변경
        // 상태 변경 직전 / 직후 로그
        DonationStatus before = donation.getStatus();
        donation.setStatus(DonationStatus.CANCEL);

        log.info("[CANCEL] status changed: {} -> {} (donationId={})",
                before,
                DonationStatus.CANCEL,
                donation.getId());
    }
}