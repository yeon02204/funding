package com.funding.funding.domain.project.service.update;

import com.funding.funding.domain.project.entity.Project;
import com.funding.funding.domain.project.entity.ProjectStatus;
import com.funding.funding.domain.project.repository.ProjectRepository;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// ✅ 순수 단위 테스트 (Mockito)
//
// [핵심 주의사항]
// new Project()를 하면 status = null 이다.
// @PrePersist는 DB 저장 시점에만 실행 → 단위 테스트에서는 동작하지 않음.
// 따라서 리플렉션으로 status 필드를 직접 세팅해야 한다.
class ProjectUpdateServiceTest {

    @Test
    void DRAFT이면_수정_검증을_통과한다() {
        // given
        ProjectRepository repo = mock(ProjectRepository.class);
        ProjectUpdateService service = new ProjectUpdateService(repo);

        Project project = new Project();
        setStatus(project, ProjectStatus.DRAFT); // ✅ DRAFT 명시
        setId(project, 1L);

        when(repo.findById(1L)).thenReturn(Optional.of(project));

        // when & then: 예외 없이 통과
        assertDoesNotThrow(() -> service.updateProject(1L));
        verify(repo).findById(1L);
    }

    @Test
    void DRAFT가_아니면_수정시_예외가_발생한다() {
        // given
        ProjectRepository repo = mock(ProjectRepository.class);
        ProjectUpdateService service = new ProjectUpdateService(repo);

        Project project = new Project();
        setStatus(project, ProjectStatus.DRAFT); // ✅ 먼저 DRAFT로 세팅 후
        setId(project, 1L);

        project.requestReview(1L); // DRAFT → REVIEW_REQUESTED (수정 불가 상태)

        when(repo.findById(1L)).thenReturn(Optional.of(project));

        // when & then
        assertThrows(IllegalStateException.class, () -> service.updateProject(1L));
        verify(repo).findById(1L);
    }

    @Test
    void 존재하지_않는_프로젝트_수정시_예외발생() {
        // given
        ProjectRepository repo = mock(ProjectRepository.class);
        ProjectUpdateService service = new ProjectUpdateService(repo);

        when(repo.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> service.updateProject(999L));
    }

    // ────────────────────────────────────────
    // 리플렉션 헬퍼
    // ────────────────────────────────────────

    private static void setId(Project project, Long id) {
        try {
            Field f = Project.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(project, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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