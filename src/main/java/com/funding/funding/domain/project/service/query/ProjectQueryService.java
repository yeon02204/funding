package com.funding.funding.domain.project.service.query;

import com.funding.funding.domain.project.entity.Project;
import com.funding.funding.domain.project.entity.ProjectStatus;
import com.funding.funding.domain.project.repository.ProjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 실제 DB 조회 로직 담당(Repository 호출 담당)
// 컨트롤러는 요청/응답만 하도록 얇게 유지

@Service // 비즈니스 로직을 담당
public class ProjectQueryService {

    private final ProjectRepository projectRepository; // DB 조회를 위한 Repository

    public ProjectQueryService(ProjectRepository projectRepository) { // 생성자 주입
        this.projectRepository = projectRepository;
    }

    // 단건 프로젝트 조회
    // readOnly = true → 조회 전용 트랜잭션 (데이터 수정 없음)
    @Transactional(readOnly = true)
    public Project getOne(Long id) { // 특정 프로젝트 하나 조회, 요청이 들어오면 Controller가 이 메서드 호출
        // id로 프로젝트 조회
        // findById는 Optional 반환 → 값이 없으면 예외 발생
        return projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다. id=" + id));
    }

    /* 기존 전체 프로젝트 조회
    @Transactional
    public List<Project> getAll() { // 모든 프로젝트 조회, 요청이 들어오면 이 Controller가 이 메서드 호출
        return projectRepository.findAll();
    }
    */

    /*
      프로젝트 목록 검색
      - 상태(status), 카테고리(categoryId), 제목(keyword) 조건으로 필터링 가능
      - Pageable을 통해 페이징(page, size)과 정렬(sort) 처리

      @param status     null이면 상태 필터 없음
      @param categoryId null이면 카테고리 필터 없음
      @param keyword    null이면 제목 검색 없음
      @param pageable   page, size, sort 포함

      사용 예:
        GET /api/projects?status=FUNDING&categoryId=2&keyword=카페&page=0&size=10&sort=createdAt,desc
     */
    @Transactional(readOnly = true)
    public Page<Project> search(
            ProjectStatus status,
            Long categoryId,
            String keyword,
            Pageable pageable
    ) {

        // keyword가 null이 아니고 빈 문자열이 아닐 때만 사용
        // "   카페   " 같은 입력은 trim()으로 공백 제거
        // 빈 문자열이면 null로 변환하여 전체 LIKE 검색 방지
        String kw = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;

        // Repository의 JPQL 검색 메서드 호출
        // 조건(status, categoryId, keyword) + 페이징(pageable)을 전달
        return projectRepository.search(status, categoryId, kw, pageable);
    }
}