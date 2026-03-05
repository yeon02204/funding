package com.funding.donation.repository;

import com.funding.donation.entity.Donation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DonationRepository extends JpaRepository<Donation, Long> {

    // 특정 프로젝트의 후원 목록
    List<Donation> findByProjectId(Long projectId);

    // 특정 유저의 후원 내역
    List<Donation> findByUserId(Long userId);

    // 성공한 후원만 조회
    List<Donation> findByStatus(String status);
}