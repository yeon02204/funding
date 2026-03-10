package com.funding.funding.domain.project.service.query;

import com.funding.funding.domain.project.entity.Project;
import com.funding.funding.domain.project.entity.ProjectStatus;
import com.funding.funding.domain.project.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// ProjectQueryService의 검색 로직 단위 테스트
class ProjectSearchServiceTest {

    private ProjectRepository projectRepository;
    private ProjectQueryService queryService;

    @BeforeEach
    void setUp() {
        projectRepository = mock(ProjectRepository.class);
        queryService = new ProjectQueryService(projectRepository);
    }

    @Test
    void 전체_조회_파라미터_없으면_null로_search_호출() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        when(projectRepository.search(null, null, null, pageable))
                .thenReturn(new PageImpl<>(List.of()));

        // when
        Page<Project> result = queryService.search(null, null, null, pageable);

        // then
        assertNotNull(result);
        verify(projectRepository).search(null, null, null, pageable);
    }

    @Test
    void status_필터_적용() {
        Pageable pageable = PageRequest.of(0, 10);
        Project p = makeProject(ProjectStatus.FUNDING);
        when(projectRepository.search(ProjectStatus.FUNDING, null, null, pageable))
                .thenReturn(new PageImpl<>(List.of(p)));

        Page<Project> result = queryService.search(ProjectStatus.FUNDING, null, null, pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void keyword가_빈문자열이면_null로_변환되어_search_호출() {
        // 빈 keyword는 null로 변환되어야 전체 검색으로 처리됨
        Pageable pageable = PageRequest.of(0, 10);
        when(projectRepository.search(null, null, null, pageable))
                .thenReturn(new PageImpl<>(List.of()));

        queryService.search(null, null, "   ", pageable); // 공백만 있는 keyword

        verify(projectRepository).search(null, null, null, pageable); // null로 전달되는지 검증
    }

    @Test
    void keyword가_있으면_trim되어_전달() {
        Pageable pageable = PageRequest.of(0, 10);
        when(projectRepository.search(null, null, "카페", pageable))
                .thenReturn(new PageImpl<>(List.of()));

        queryService.search(null, null, "  카페  ", pageable); // 공백 포함

        verify(projectRepository).search(null, null, "카페", pageable); // trim 확인
    }

    private Project makeProject(ProjectStatus status) {
        Project p = new Project();
        setField(p, "status", status);
        return p;
    }

    private static void setField(Object t, String name, Object value) {
        try {
            Field f = t.getClass().getDeclaredField(name);
            f.setAccessible(true);
            f.set(t, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}