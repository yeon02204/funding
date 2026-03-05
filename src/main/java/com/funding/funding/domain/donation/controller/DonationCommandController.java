package com.funding.funding.domain.donation.controller;

import com.funding.funding.domain.donation.entity.Donation;
import com.funding.funding.domain.donation.service.cancel.DonationCancelService;
import com.funding.funding.domain.donation.service.pay.DonationPayService;
import com.funding.funding.domain.donation.service.refund.DonationRefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/*
 * ⚠️ 임시 디버깅 전용 컨트롤러
 *
 * [목적]
 * - Postman 대신 브라우저에서 버튼으로 도메인 상태 전이 테스트
 * - 통합 전 단계에서 후원 흐름 검증
 *
 * [현재 역할]
 * - createDonation
 * - SUCCESS 처리
 * - CANCEL 처리
 * - REFUND 처리
 *
 * [주의]
 * - 인증 미적용
 * - 권한 검증 없음
 * - 실서비스용 아님
 *
 * [향후 발전 방향]
 * 1. Security 적용 (@AuthenticationPrincipal 사용)
 * 2. DTO 기반 요청 구조로 변경
 * 3. 예외 응답 통일 (GlobalExceptionHandler)
 * 4. 관리자 환불은 별도 AdminController로 분리
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/donations")
public class DonationCommandController {

    private final DonationPayService payService;
    private final DonationCancelService cancelService;
    private final DonationRefundService refundService;

    /*
     * 후원 생성 (PENDING 상태)
     *
     * 지금은 단순 테스트용이라
     * userId / projectId를 RequestParam으로 받는다.
     *
     * 👉 실서비스에서는 로그인 사용자 ID를 Security에서 가져와야 함.
     */
    @PostMapping
    public Donation create(
            @RequestParam Long userId,
            @RequestParam Long projectId,
            @RequestParam Long amount
    ) {
        return payService.createDonation(userId, projectId, amount);
    }

    /*
     * 결제 성공 처리
     * PENDING → SUCCESS
     */
    @PostMapping("/{id}/success")
    public void markSuccess(@PathVariable Long id) {
        payService.markSuccess(id);
    }

    /*
     * 사용자 취소
     * SUCCESS → CANCEL
     * 24시간 이내만 허용
     */
    @PostMapping("/{id}/cancel")
    public void cancel(@PathVariable Long id) {
        cancelService.cancel(id);
    }

    /*
     * 환불 처리
     * SUCCESS → REFUND
     *
     * ⚠️ 현재는 관리자/통합 처리 구분 없음
     * 향후 AdminDonationController로 분리 예정
     */
    @PostMapping("/{id}/refund")
    public void refund(@PathVariable Long id) {
        refundService.refund(id);
    }
}