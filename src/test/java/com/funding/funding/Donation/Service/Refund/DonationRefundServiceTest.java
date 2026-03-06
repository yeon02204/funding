package com.funding.funding.Donation.Service.Refund;

import com.funding.funding.domain.donation.entity.Donation;
import com.funding.funding.domain.donation.repository.DonationRepository;
import com.funding.funding.domain.donation.service.refund.DonationRefundService;
import com.funding.funding.domain.donation.status.DonationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// ✅ 순수 단위 테스트 (Mockito)
// - @SpringBootTest → Mockito mock()으로 전환
//   이유: Donation 엔티티의 user_id, project_id, amount가 NOT NULL이라
//        new Donation()만으로는 실제 DB에 저장 불가능
// - Repository를 mock으로 교체해 DB 없이도 서비스 로직만 검증
class DonationRefundServiceTest {

    private DonationRepository donationRepository;
    private DonationRefundService refundService;

    @BeforeEach
    void setUp() {
        donationRepository = mock(DonationRepository.class);
        refundService = new DonationRefundService(donationRepository);
    }

    /*
     * 정상 케이스
     * - 상태: SUCCESS
     * - 기대: 상태 REFUND로 변경 + refundedAt 기록
     */
    @Test
    void success_to_refund() {
        // given
        Donation donation = createDonation(1L, DonationStatus.SUCCESS);
        when(donationRepository.findById(1L)).thenReturn(Optional.of(donation));

        // when
        refundService.refund(1L);

        // then
        assertEquals(DonationStatus.REFUND, donation.getStatus());
        assertNotNull(donation.getRefundedAt()); // 환불 시간 기록 확인
    }

    /*
     * 실패 케이스: 이미 REFUND 상태
     * - DonationStatus.canTransitionTo() → REFUND에서 REFUND 불가
     * - 기대: IllegalStateException 발생
     */
    @Test
    void refund_fail_when_already_refunded() {
        // given
        Donation donation = createDonation(1L, DonationStatus.REFUND);
        when(donationRepository.findById(1L)).thenReturn(Optional.of(donation));

        // when & then
        assertThrows(IllegalStateException.class, () -> refundService.refund(1L));
    }

    /*
     * 실패 케이스: PENDING 상태
     * - PENDING → REFUND 전이 불허 (PENDING은 SUCCESS/FAILED로만 전이 가능)
     * - 기대: IllegalStateException 발생
     */
    @Test
    void refund_fail_when_status_is_pending() {
        // given
        Donation donation = createDonation(1L, DonationStatus.PENDING);
        when(donationRepository.findById(1L)).thenReturn(Optional.of(donation));

        // when & then
        assertThrows(IllegalStateException.class, () -> refundService.refund(1L));
    }

    /*
     * 실패 케이스: CANCEL 상태
     * - 취소된 후원은 환불 불가
     * - 기대: IllegalStateException 발생
     */
    @Test
    void refund_fail_when_status_is_cancel() {
        // given
        Donation donation = createDonation(1L, DonationStatus.CANCEL);
        when(donationRepository.findById(1L)).thenReturn(Optional.of(donation));

        // when & then
        assertThrows(IllegalStateException.class, () -> refundService.refund(1L));
    }

    /*
     * 실패 케이스: 존재하지 않는 후원
     * - 기대: IllegalArgumentException 발생
     */
    @Test
    void refund_fail_when_donation_not_found() {
        // given
        when(donationRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> refundService.refund(999L));
    }

    // ────────────────────────────────────────
    // 헬퍼
    // ────────────────────────────────────────
    private Donation createDonation(Long id, DonationStatus status) {
        Donation donation = new Donation();
        setField(donation, "id", id);
        donation.setStatus(status);
        return donation;
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            Field f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}