package com.funding.funding.domain.project.repository;

import com.funding.funding.domain.project.entity.Project;
import com.funding.funding.domain.project.entity.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    // APPROVED 상태이면서 시작일이 도달한 프로젝트 조회
    // - 스케줄러가 APPROVED → FUNDING 전환 시 사용
    List<Project> findByStatusAndStartAtLessThanEqual(ProjectStatus status, LocalDateTime now);

    // FUNDING 상태이면서 마감일이 지난 프로젝트 조회
    // - 스케줄러가 FUNDING → SUCCESS/FAILED 전환 시 사용
    List<Project> findByStatusAndDeadlineLessThanEqual(ProjectStatus status, LocalDateTime now);
}