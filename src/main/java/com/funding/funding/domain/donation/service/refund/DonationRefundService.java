package com.funding.funding.domain.donation.service.refund;

import com.funding.funding.domain.donation.entity.Donation;
import com.funding.funding.domain.donation.repository.DonationRepository;
import com.funding.funding.domain.donation.status.DonationStatus;
import com.funding.funding.domain.project.entity.Project;
import com.funding.funding.domain.project.repository.ProjectRepository;
import com.funding.funding.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class DonationRefundService {

    private final DonationRepository donationRepository;
    private final ProjectRepository  projectRepository;

    // 단건 환불
    @Transactional
    public void refund(Long donationId) {
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new IllegalArgumentException("Donation not found"));

        if (!donation.getStatus().canTransitionTo(DonationStatus.REFUND)) {
            throw new IllegalStateException("Invalid state transition");
        }

        donation.getProject().decreaseCurrentAmount(donation.getAmount());
        donation.setStatus(DonationStatus.REFUND);
        donation.setRefundedAt(LocalDateTime.now());
    }

    // 프로젝트 전체 환불 (관리자)
    @Transactional
    public int refundAllByProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "프로젝트를 찾을 수 없습니다."));

        List<Donation> targets = donationRepository.findByProjectIdOrderByCreatedAtDesc(projectId)
                .stream()
                .filter(d -> d.getStatus() == DonationStatus.SUCCESS)
                .toList();

        LocalDateTime now = LocalDateTime.now();
        for (Donation d : targets) {
            project.decreaseCurrentAmount(d.getAmount());
            d.setStatus(DonationStatus.REFUND);
            d.setRefundedAt(now);
        }

        return targets.size();
    }
}