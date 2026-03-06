package com.funding.funding.Donation.Service.Cancel;

import com.funding.funding.domain.donation.entity.Donation;
import com.funding.funding.domain.donation.repository.DonationRepository;
import com.funding.funding.domain.donation.service.cancel.DonationCancelService;
import com.funding.funding.domain.donation.status.DonationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// ✅ 순수 단위 테스트 (Mockito)
// - @SpringBootTest → Mockito mock()으로 전환
//   이유: Donation 엔티티의 user_id, project_id, amount가 NOT NULL이라
//        new Donation()만으로는 실제 DB에 저장 불가능
// - Repository를 mock으로 교체해 DB 없이도 서비스 로직만 검증
class DonationCancelServiceTest {

    private DonationRepository donationRepository;
    private DonationCancelService cancelService;

    @BeforeEach
    void setUp() {
        donationRepository = mock(DonationRepository.class);
        cancelService = new DonationCancelService(donationRepository);
    }

    /*
     * 정상 케이스
     * - 상태: SUCCESS, cancelDeadline: 현재 이후
     * - 기대: 상태가 CANCEL로 변경
     */
    @Test
    void success_to_cancel_within_deadline() {
        // given
        Donation donation = createDonation(1L, DonationStatus.SUCCESS, LocalDateTime.now().plusHours(1));
        when(donationRepository.findById(1L)).thenReturn(Optional.of(donation));

        // when
        cancelService.cancel(1L);

        // then: 상태가 CANCEL로 변경되었는지 확인
        assertEquals(DonationStatus.CANCEL, donation.getStatus());
    }

    /*
     * 실패 케이스: 취소 마감 시간 초과
     * - 상태: SUCCESS, cancelDeadline: 이미 지남
     * - 기대: IllegalStateException 발생
     */
    @Test
    void cancel_fail_when_deadline_passed() {
        // given
        Donation donation = createDonation(1L, DonationStatus.SUCCESS, LocalDateTime.now().minusHours(1));
        when(donationRepository.findById(1L)).thenReturn(Optional.of(donation));

        // when & then
        assertThrows(IllegalStateException.class, () -> cancelService.cancel(1L));
    }

    /*
     * 실패 케이스: 이미 CANCEL 상태
     * - DonationStatus.canTransitionTo() → CANCEL에서 CANCEL 불가 → false
     * - 기대: IllegalStateException 발생
     */
    @Test
    void cancel_fail_when_already_cancelled() {
        // given
        Donation donation = createDonation(1L, DonationStatus.CANCEL, LocalDateTime.now().plusHours(1));
        when(donationRepository.findById(1L)).thenReturn(Optional.of(donation));

        // when & then
        assertThrows(IllegalStateException.class, () -> cancelService.cancel(1L));
    }

    /*
     * 실패 케이스: PENDING 상태
     * - PENDING → CANCEL 전이 불허 (PENDING은 SUCCESS/FAILED로만 전이 가능)
     * - 기대: IllegalStateException 발생
     */
    @Test
    void cancel_fail_when_status_is_pending() {
        // given
        Donation donation = createDonation(1L, DonationStatus.PENDING, LocalDateTime.now().plusHours(1));
        when(donationRepository.findById(1L)).thenReturn(Optional.of(donation));

        // when & then
        assertThrows(IllegalStateException.class, () -> cancelService.cancel(1L));
    }

    /*
     * 실패 케이스: 존재하지 않는 후원
     * - 기대: IllegalArgumentException 발생
     */
    @Test
    void cancel_fail_when_donation_not_found() {
        // given
        when(donationRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> cancelService.cancel(999L));
    }

    // ────────────────────────────────────────
    // 헬퍼: Donation 테스트 객체 생성 (DB 없이)
    // ────────────────────────────────────────
    private Donation createDonation(Long id, DonationStatus status, LocalDateTime cancelDeadline) {
        Donation donation = new Donation();
        setField(donation, "id", id);
        donation.setStatus(status);
        donation.setCancelDeadline(cancelDeadline);
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