package com.funding.funding.domain.project.repository;

import com.funding.funding.domain.project.entity.ProjectTag;
import com.funding.funding.domain.project.entity.ProjectTagId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectTagRepository extends JpaRepository<ProjectTag, ProjectTagId> {

    // 특정 프로젝트의 태그 이름 목록
    @Query("SELECT pt.tag.name FROM ProjectTag pt WHERE pt.project.id = :projectId")
    List<String> findTagNamesByProjectId(@Param("projectId") Long projectId);

    // 특정 프로젝트의 태그 전체 삭제 (태그 교체 시 사용)
    void deleteByProjectId(Long projectId);
}