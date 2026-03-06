package com.funding.funding.domain.donation.controller;

import com.funding.funding.domain.donation.dto.AdminDonationResponse;
import com.funding.funding.domain.donation.service.query.DonationQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

// 관리자 전용 후원 전체 목록 조회 컨트롤러
// [TODO] 인증 연동 후 @PreAuthorize("hasRole('ADMIN')") 추가

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/donations") // ✅ /api/ prefix 통일
public class DonationAdminController {

    private final DonationQueryService donationQueryService;

    // 전체 후원 목록 페이징 조회
    @GetMapping
    public Page<AdminDonationResponse> findAll(
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return donationQueryService.findAllDonations(pageable);
    }
}