package com.funding.funding.domain.donation.controller;

import com.funding.funding.domain.donation.entity.Donation;
import com.funding.funding.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.funding.funding.domain.donation.service.cancel.DonationCancelService;
import com.funding.funding.domain.donation.service.pay.DonationPayService;
import com.funding.funding.domain.donation.service.refund.DonationRefundService;
import com.funding.funding.global.exception.ApiException;
import com.funding.funding.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "후원 명령", description = "후원하기, 취소, 환불")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DonationCommandController {

    private final DonationPayService donationPayService;
    private final DonationCancelService donationCancelService;
    private final DonationRefundService donationRefundService;

    @PostMapping("/projects/{projectId}/donations")
    public ApiResponse<Void> donate(
            @PathVariable Long projectId,
            @RequestParam Long amount,
            Authentication auth
    ) {
        Long userId = extractUserId(auth);

        Donation donation = donationPayService.createDonation(userId, projectId, amount);
        donationPayService.markSuccess(donation.getId());

        return ApiResponse.ok(null);
    }

    @PostMapping("/donations/{donationId}/cancel")
    public ApiResponse<Void> cancelDonation(
            @PathVariable Long donationId,
            Authentication auth
    ) {
        Long userId = extractUserId(auth);
        donationCancelService.cancel(donationId, userId);
        return ApiResponse.ok(null);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/donations/{donationId}/refund")
    public ApiResponse<Void> refundDonation(@PathVariable Long donationId) {
        donationRefundService.refund(donationId);
        return ApiResponse.ok(null);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/projects/{projectId}/refund-all")
    public ApiResponse<String> refundAllByProject(@PathVariable Long projectId) {
        int count = donationRefundService.refundAllByProject(projectId);
        return ApiResponse.ok(count + "건 환불 처리 완료");
    }

    // ────────────────────────────────────────
    private Long extractUserId(Authentication auth) {
        if (auth == null || auth.getPrincipal() == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof Long id) return id;
        if (principal instanceof CustomUserDetails u) return u.getUserId();
        if (principal instanceof String s) return Long.valueOf(s);
        throw new ApiException(HttpStatus.UNAUTHORIZED, "인증 정보가 올바르지 않습니다.");
    }
}