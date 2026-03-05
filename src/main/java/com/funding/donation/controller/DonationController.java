package com.funding.donation.controller;

import com.funding.donation.entity.Donation;
import com.funding.donation.service.DonationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/donations")
public class DonationController {

    private final DonationService donationService;

    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    // ✅ 후원하기
    @PostMapping
    public ResponseEntity<Donation> donate(
            @RequestParam Long userId,
            @RequestParam Long projectId,
            @RequestParam Long amount
    ) {
        Donation donation = donationService.donate(userId, projectId, amount);
        return ResponseEntity.ok(donation);
    }

    // ✅ 내 후원 내역 조회
    @GetMapping("/me/{userId}")
    public ResponseEntity<List<Donation>> myDonations(@PathVariable Long userId) {
        return ResponseEntity.ok(donationService.getMyDonations(userId));
    }

    // ✅ 프로젝트별 후원 조회
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Donation>> projectDonations(@PathVariable Long projectId) {
        return ResponseEntity.ok(donationService.getProjectDonations(projectId));
    }

    // ✅ 후원 취소
    @PostMapping("/{donationId}/cancel")
    public ResponseEntity<String> cancel(@PathVariable Long donationId) {
        donationService.cancelDonation(donationId);
        return ResponseEntity.ok("후원 취소 완료");
    }

    // ✅ 환불
    @PostMapping("/{donationId}/refund")
    public ResponseEntity<String> refund(@PathVariable Long donationId) {
        donationService.refundDonation(donationId);
        return ResponseEntity.ok("환불 처리 완료");
    }
}