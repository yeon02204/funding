package com.funding.funding.Donation.Service.OptimisticLock;

import com.funding.funding.domain.category.entity.Category;
import com.funding.funding.domain.category.repository.CategoryRepository;
import com.funding.funding.domain.donation.entity.Donation;
import com.funding.funding.domain.donation.repository.DonationRepository;
import com.funding.funding.domain.donation.status.DonationStatus;
import com.funding.funding.domain.project.entity.Project;
import com.funding.funding.domain.project.entity.ProjectStatus;
import com.funding.funding.domain.project.repository.ProjectRepository;
import com.funding.funding.domain.user.entity.AuthProvider;
import com.funding.funding.domain.user.entity.User;
import com.funding.funding.domain.user.entity.UserRole;
import com.funding.funding.domain.user.entity.UserStatus;
import com.funding.funding.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Optimistic Lock 동시성 테스트
 *
 * 목적:
 * - 동일한 Donation을 두 영속성 컨텍스트가 동시에 수정할 경우
 *   나중에 flush하는 쪽이 ObjectOptimisticLockingFailureException을 던지는지 검증
 *
 * 왜 @SpringBootTest인가:
 * - @Version 동작은 실제 JPA/Hibernate + DB가 있어야 검증 가능
 * - Mock으로는 version 충돌 시뮬레이션 불가
 *
 * 주의:
 * - @Transactional을 걸면 안 됨 (같은 트랜잭션 내에서는 같은 인스턴스 반환)
 *   → saveAndFlush를 별도 트랜잭션으로 나눠야 version 충돌이 발생
 */
@SpringBootTest
class DonationOptimisticLockTest {

    @Autowired private DonationRepository donationRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private CategoryRepository categoryRepository;

    private Long donationId;

    @BeforeEach
    void setUp() {
        // ✅ Donation 저장을 위해 필수 연관 엔티티 먼저 생성
        // 1. User
        User user = new User(
                "test_" + System.currentTimeMillis() + "@example.com",
                "tester" + System.currentTimeMillis(),
                "password",
                UserRole.USER,
                UserStatus.ACTIVE,
                AuthProvider.LOCAL
        );
        userRepository.save(user);

        // 2. Category
        Category category = createCategory("테스트카테고리" + System.currentTimeMillis());
        categoryRepository.save(category);

        // 3. Project
        Project project = Project.create(
                user,
                category,
                "테스트 프로젝트",
                "본문",
                100_000L,
                LocalDateTime.now().plusDays(30)
        );
        // status를 FUNDING으로 세팅해야 후원 가능
        setField(project, "status", ProjectStatus.FUNDING);
        projectRepository.save(project);

        // 4. Donation
        Donation donation = new Donation();
        donation.setUser(user);
        donation.setProject(project);
        donation.setAmount(10_000L);
        donation.setStatus(DonationStatus.SUCCESS);
        donation.setCancelDeadline(LocalDateTime.now().plusHours(1));
        donationRepository.saveAndFlush(donation);

        donationId = donation.getId();
    }

    @Test
    void optimistic_lock_should_fail_on_concurrent_update() {
        // 1. 같은 레코드를 두 번 조회 (각각 다른 인스턴스)
        Donation firstRead  = donationRepository.findById(donationId).get();
        Donation secondRead = donationRepository.findById(donationId).get();

        // 2. 첫 번째 수정 → 정상 저장 (version: 0 → 1)
        firstRead.setStatus(DonationStatus.CANCEL);
        donationRepository.saveAndFlush(firstRead);

        // 3. 두 번째 수정 시도 → version 0으로 수정 시도하지만 DB는 이미 version 1
        //    → ObjectOptimisticLockingFailureException 발생해야 함
        secondRead.setStatus(DonationStatus.REFUND);

        assertThrows(ObjectOptimisticLockingFailureException.class, () ->
                donationRepository.saveAndFlush(secondRead)
        );
    }

    // ────────────────────────────────────────
    // 헬퍼
    // ────────────────────────────────────────
    private static Category createCategory(String name) {
        try {
            Category c = new Category();
            Field nameField = Category.class.getDeclaredField("name");
            nameField.setAccessible(true);
            nameField.set(c, name);
            return c;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            // 상위 클래스까지 탐색
            Class<?> clazz = target.getClass();
            while (clazz != null) {
                try {
                    Field f = clazz.getDeclaredField(fieldName);
                    f.setAccessible(true);
                    f.set(target, value);
                    return;
                } catch (NoSuchFieldException e) {
                    clazz = clazz.getSuperclass();
                }
            }
            throw new NoSuchFieldException(fieldName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}