package com.funding.donation.service;

import com.funding.donation.entity.Donation;
import com.funding.donation.repository.DonationRepository;
import com.funding.project.entity.Project;
import com.funding.project.repository.ProjectRepository;
import com.funding.user.entity.User;
import com.funding.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class DonationService {

    private final DonationRepository donationRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public DonationService(DonationRepository donationRepository,
                           UserRepository userRepository,
                           ProjectRepository projectRepository) {
        this.donationRepository = donationRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    // ✅ 후원하기
    public Donation donate(Long userId, Long projectId, Long amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트 없음"));

        // 프로젝트 상태 검증
        if (!project.getStatus().equals("FUNDING")) {
            throw new IllegalStateException("후원 가능한 상태가 아님");
        }

        Donation donation = new Donation(
                user,
                project,
                amount,
                "PENDING",
                LocalDateTime.now().plusHours(24),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        return donationRepository.save(donation);
    }

    // ✅ 특정 유저 후원 목록
    public List<Donation> getMyDonations(Long userId) {
        return donationRepository.findByUserId(userId);
    }

    // ✅ 프로젝트 후원 목록
    public List<Donation> getProjectDonations(Long projectId) {
        return donationRepository.findByProjectId(projectId);
    }

    // ✅ 후원 취소
    public void cancelDonation(Long donationId) {
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new IllegalArgumentException("후원 없음"));

        if (donation.getCancelDeadline().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("취소 가능 시간 초과");
        }

        donation.setStatus("CANCEL");
        donation.setUpdatedAt(LocalDateTime.now());
    }

    // ✅ 환불 처리
    public void refundDonation(Long donationId) {
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new IllegalArgumentException("후원 없음"));

        donation.setStatus("REFUND");
        donation.setRefundedAt(LocalDateTime.now());
        donation.setUpdatedAt(LocalDateTime.now());
    }
}