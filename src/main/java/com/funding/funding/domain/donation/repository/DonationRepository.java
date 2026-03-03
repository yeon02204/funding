package com.funding.funding.domain.donation.repository;

import com.funding.funding.domain.donation.entity.Donation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonationRepository extends JpaRepository<Donation, Long>{

}
