package com.funding.funding.domain.project.entity;

import com.funding.funding.domain.project.exception.InvalidProjectStatusTransitionException;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

// ✅ 순수 단위 테스트 - Spring Context 불필요
//
// [핵심 주의사항]
// new Project()를 하면 status = null 이다.
// @PrePersist는 JPA가 DB에 저장할 때만 실행 → 단위 테스트에서는 동작하지 않음.
// 따라서 모든 테스트에서 newDraftProject()를 통해 status = DRAFT로 초기화해야 한다.
class ProjectTest {

    // ────────────────────────────────────────
    // 헬퍼
    // ────────────────────────────────────────

    /** new Project() + status = DRAFT 세팅 */
    private Project newDraftProject() {
        Project project = new Project();
        setStatus(project, ProjectStatus.DRAFT);
        return project;
    }

    /** DRAFT → REVIEW_REQUESTED → APPROVED → FUNDING */
    private Project createFundingProject() {
        Project project = newDraftProject();
        project.requestReview(1L);
        project.changeStatus(ProjectStatus.APPROVED, "ADMIN", 99L);
        project.changeStatus(ProjectStatus.FUNDING, "SYSTEM", 0L);
        return project;
    }

    /** DRAFT → REVIEW_REQUESTED → REJECTED */
    private Project createRejectedProject() {
        Project project = newDraftProject();
        project.requestReview(1L);
        project.changeStatus(ProjectStatus.REJECTED, "ADMIN", 99L);
        return project;
    }

    // ────────────────────────────────────────
    // 정상 전이 케이스
    // ────────────────────────────────────────

    @Test
    void DRAFT에서_심사요청하면_REVIEW_REQUESTED로_변경된다() {
        Project project = newDraftProject();

        project.requestReview(1L);

        assertEquals(ProjectStatus.REVIEW_REQUESTED, project.getStatus());
    }

    @Test
    void REVIEW_REQUESTED에서_APPROVED로_변경된다() {
        Project project = newDraftProject();
        project.requestReview(1L);

        project.changeStatus(ProjectStatus.APPROVED, "ADMIN", 99L);

        assertEquals(ProjectStatus.APPROVED, project.getStatus());
    }

    @Test
    void REVIEW_REQUESTED에서_REJECTED로_변경된다() {
        Project project = newDraftProject();
        project.requestReview(1L);

        project.changeStatus(ProjectStatus.REJECTED, "ADMIN", 99L);

        assertEquals(ProjectStatus.REJECTED, project.getStatus());
    }

    @Test
    void REJECTED에서_재심사요청하면_REVIEW_REQUESTED로_변경된다() {
        Project project = createRejectedProject();

        project.requestReview(1L);

        assertEquals(ProjectStatus.REVIEW_REQUESTED, project.getStatus());
    }

    @Test
    void APPROVED에서_FUNDING으로_변경된다() {
        Project project = newDraftProject();
        project.requestReview(1L);
        project.changeStatus(ProjectStatus.APPROVED, "ADMIN", 99L);

        project.changeStatus(ProjectStatus.FUNDING, "SYSTEM", 0L);

        assertEquals(ProjectStatus.FUNDING, project.getStatus());
    }

    @Test
    void FUNDING에서_STOPPED로_변경된다() {
        Project project = createFundingProject();

        project.changeStatus(ProjectStatus.STOPPED, "ADMIN", 99L);

        assertEquals(ProjectStatus.STOPPED, project.getStatus());
    }

    @Test
    void STOPPED에서_FUNDING으로_재개된다() {
        Project project = createFundingProject();
        project.changeStatus(ProjectStatus.STOPPED, "ADMIN", 99L);

        project.changeStatus(ProjectStatus.FUNDING, "ADMIN", 99L);

        assertEquals(ProjectStatus.FUNDING, project.getStatus());
    }

    @Test
    void FUNDING에서_DELETE_REQUESTED로_변경된다() {
        Project project = createFundingProject();

        project.changeStatus(ProjectStatus.DELETE_REQUESTED, "USER", 1L);

        assertEquals(ProjectStatus.DELETE_REQUESTED, project.getStatus());
    }

    @Test
    void DELETE_REQUESTED에서_DELETED로_변경된다() {
        Project project = createFundingProject();
        project.changeStatus(ProjectStatus.DELETE_REQUESTED, "USER", 1L);

        project.changeStatus(ProjectStatus.DELETED, "SYSTEM", 0L);

        assertEquals(ProjectStatus.DELETED, project.getStatus());
    }

    // ────────────────────────────────────────
    // 실패(예외) 케이스
    // ────────────────────────────────────────

    @Test
    void FUNDING에서_심사요청하면_예외가_발생한다() {
        Project project = createFundingProject();

        assertThrows(InvalidProjectStatusTransitionException.class,
                () -> project.requestReview(1L));
    }

    @Test
    void DRAFT에서_APPROVED로_바로가면_예외() {
        Project project = newDraftProject();

        assertThrows(InvalidProjectStatusTransitionException.class,
                () -> project.changeStatus(ProjectStatus.APPROVED, "ADMIN", 99L));
    }

    @Test
    void SUCCESS는_종결상태라서_다른상태로_전이불가() {
        Project project = createFundingProject();
        project.changeStatus(ProjectStatus.SUCCESS, "SYSTEM", 0L);

        assertThrows(InvalidProjectStatusTransitionException.class,
                () -> project.changeStatus(ProjectStatus.FUNDING, "ADMIN", 99L));
    }

    @Test
    void FAILED는_종결상태라서_다른상태로_전이불가() {
        Project project = createFundingProject();
        project.changeStatus(ProjectStatus.FAILED, "SYSTEM", 0L);

        assertThrows(InvalidProjectStatusTransitionException.class,
                () -> project.changeStatus(ProjectStatus.FUNDING, "ADMIN", 99L));
    }

    // ────────────────────────────────────────
    // 리플렉션 헬퍼
    // ────────────────────────────────────────

    private static void setStatus(Project project, ProjectStatus status) {
        try {
            Field f = Project.class.getDeclaredField("status");
            f.setAccessible(true);
            f.set(project, status);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}