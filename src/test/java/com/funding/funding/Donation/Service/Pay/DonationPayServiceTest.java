package com.funding.funding.Donation.Service.Pay;

import com.funding.funding.domain.donation.entity.Donation;
import com.funding.funding.domain.donation.repository.DonationRepository;
import com.funding.funding.domain.donation.service.pay.DonationPayService;
import com.funding.funding.domain.donation.status.DonationStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class DonationPayServiceTest {

    @Autowired
    private DonationPayService payService;

    @Autowired
    private DonationRepository donationRepository;

    @Test
    void create_donation_success() {

        Donation donation = payService.createDonation(1L, 1L, 5000L);

        assertEquals(DonationStatus.PENDING, donation.getStatus());
        assertEquals(5000L, donation.getAmount());
        assertNotNull(donation.getCancelDeadline());
    }

    @Test
    void create_donation_fail_when_invalid_amount() {

        assertThrows(IllegalArgumentException.class, () ->
                payService.createDonation(1L, 1L, 500L)
        );

        assertThrows(IllegalArgumentException.class, () ->
                payService.createDonation(1L, 1L, 1500L)
        );
    }

    @Test
    void mark_success_from_pending() {

        Donation donation = payService.createDonation(1L, 1L, 5000L);

        payService.markSuccess(donation.getId());

        Donation result = donationRepository.findById(donation.getId()).get();

        assertEquals(DonationStatus.SUCCESS, result.getStatus());
    }

    @Test
    void mark_success_fail_when_invalid_state() {

        Donation donation = payService.createDonation(1L, 1L, 5000L);

        payService.markSuccess(donation.getId());

        assertThrows(IllegalStateException.class, () ->
                payService.markSuccess(donation.getId())
        );
    }

    @Test
    void mark_failed_from_pending() {

        Donation donation = payService.createDonation(1L, 1L, 5000L);

        payService.markFailed(donation.getId());

        Donation result = donationRepository.findById(donation.getId()).get();

        assertEquals(DonationStatus.FAILED, result.getStatus());
    }

    @Test
    void mark_failed_fail_when_invalid_state() {

        Donation donation = payService.createDonation(1L, 1L, 5000L);

        payService.markSuccess(donation.getId());

        assertThrows(IllegalStateException.class, () ->
                payService.markFailed(donation.getId())
        );
    }
}