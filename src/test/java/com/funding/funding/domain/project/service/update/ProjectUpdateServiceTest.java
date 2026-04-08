package com.funding.funding.domain.project.service.update;

import com.funding.funding.domain.project.dto.ProjectUpdateRequest;
import com.funding.funding.domain.project.entity.Project;
import com.funding.funding.domain.project.entity.ProjectStatus;
import com.funding.funding.domain.project.repository.ProjectRepository;
import com.funding.funding.domain.user.entity.User;
import com.funding.funding.global.exception.ApiException;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// ✅ 순수 단위 테스트 (Mockito)
//
// [핵심 주의사항]
// new Project()를 하면 status = null 이다.
// @PrePersist는 DB 저장 시점에만 실행 → 단위 테스트에서는 동작하지 않음.
// 따라서 리플렉션으로 status, owner 필드를 직접 세팅해야 한다.
class ProjectUpdateServiceTest {

    // 유효한 수정 요청 (goalAmount > 0, deadline 미래)
    private static final ProjectUpdateRequest VALID_REQUEST = new ProjectUpdateRequest(
            "수정된 제목", "수정된 내용", 100_000L,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(30),
            1L
    );

    @Test
    void DRAFT이면_수정_검증을_통과한다() {
        // given
        ProjectRepository repo = mock(ProjectRepository.class);
        ProjectUpdateService service = new ProjectUpdateService(repo);

        User owner = mockUserWithId(1L);
        Project project = new Project();
        setField(project, "status", ProjectStatus.DRAFT);
        setField(project, "id", 1L);
        setField(project, "owner", owner);

        when(repo.findById(1L)).thenReturn(Optional.of(project));

        // when & then: 예외 없이 통과
        assertDoesNotThrow(() -> service.updateProject(1L, 1L, VALID_REQUEST));
        verify(repo).findById(1L);
    }

    @Test
    void DRAFT가_아니면_수정시_예외가_발생한다() {
        // given
        ProjectRepository repo = mock(ProjectRepository.class);
        ProjectUpdateService service = new ProjectUpdateService(repo);

        User owner = mockUserWithId(1L);
        Project project = new Project();
        setField(project, "status", ProjectStatus.DRAFT);
        setField(project, "id", 1L);
        setField(project, "owner", owner);

        project.requestReview(1L); // DRAFT → REVIEW_REQUESTED (수정 불가 상태)

        when(repo.findById(1L)).thenReturn(Optional.of(project));

        // when & then
        assertThrows(IllegalStateException.class,
                () -> service.updateProject(1L, 1L, VALID_REQUEST));
        verify(repo).findById(1L);
    }

    @Test
    void 존재하지_않는_프로젝트_수정시_예외발생() {
        // given
        ProjectRepository repo = mock(ProjectRepository.class);
        ProjectUpdateService service = new ProjectUpdateService(repo);

        when(repo.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> service.updateProject(999L, 1L, VALID_REQUEST));
    }

    @Test
    void 소유자가_아니면_수정시_FORBIDDEN_예외가_발생한다() {
        // given
        ProjectRepository repo = mock(ProjectRepository.class);
        ProjectUpdateService service = new ProjectUpdateService(repo);

        User owner = mockUserWithId(1L);   // 프로젝트 소유자: userId=1
        Project project = new Project();
        setField(project, "status", ProjectStatus.DRAFT);
        setField(project, "id", 1L);
        setField(project, "owner", owner);

        when(repo.findById(1L)).thenReturn(Optional.of(project));

        // when & then: 다른 사용자(userId=2)가 수정 시도 → FORBIDDEN
        ApiException ex = assertThrows(ApiException.class,
                () -> service.updateProject(1L, 2L, VALID_REQUEST));
        assertEquals(403, ex.getStatus().value());
    }

    // ────────────────────────────────────────
    // 헬퍼
    // ────────────────────────────────────────

    private static User mockUserWithId(Long id) {
        User user = mock(User.class);
        when(user.getId()).thenReturn(id);
        return user;
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            Field f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}